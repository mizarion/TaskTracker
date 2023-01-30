package com.consist.taskboot.resttemplate;

import com.consist.taskboot.model.Status;
import com.consist.taskboot.model.dto.TaskDto;
import com.consist.taskboot.model.dto.TaskParameterDto;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("DataFlowIssue")
@Testcontainers
class RestTemplateTestContainerIT {
    private static final Integer FREE_ID = 999;
    private static final int EXPOSED_PORT = 8080;
    private final static TaskDto taskDto = new TaskDto(FREE_ID, Status.READY, "test");
    private final String url;
    private final RestTemplate restTemplate;

    RestTemplateTestContainerIT() {
        restTemplate = new RestTemplate();
        url = buildURI(tomcat);
    }

    @SuppressWarnings("resource")
    @Container
    private static final GenericContainer<?> tomcat = new GenericContainer<>(
            new ImageFromDockerfile()
                    .withDockerfileFromBuilder(builder -> builder
                            .from("tomcat:9")
                            .run("rm -rf /usr/local/tomcat/webapps/ROOT")
                            .copy("ROOT.war", "/usr/local/tomcat/webapps/ROOT.war")
                            .env("spring.datasource.url", "jdbc:h2:mem:consistdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;NON_KEYWORDS=value")
                            .env("spring.datasource.driver-class-name", "org.h2.Driver")
                            .env("spring.datasource.username", "username")
                            .env("spring.datasource.password", "password")
                    )
                    .withFileFromPath("ROOT.war", Paths.get("target/taskboot.war")))
            .withExposedPorts(EXPOSED_PORT);

    private static String buildURI(GenericContainer<?> container) {
        return "http://" + container.getHost() + ":" + container.getMappedPort(EXPOSED_PORT) + "/tasks";
    }

    @AfterEach
    void delete() {
        restTemplate.delete(url + "?id=" + taskDto.getId());
    }

    @Test
    @Order(1)
    void testCRD() {
        ResponseEntity<TaskDto> request = restTemplate.postForEntity(url, taskDto, TaskDto.class);
        Assertions.assertEquals(HttpStatus.ACCEPTED, request.getStatusCode());

        ResponseEntity<TaskDto> response = restTemplate.getForEntity(url + "?id=" + taskDto.getId(), TaskDto.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(taskDto, response.getBody());
    }

    @Test
    void testCRUD() {
        ResponseEntity<TaskDto> request = restTemplate.postForEntity(url, taskDto, TaskDto.class);
        Assertions.assertEquals(HttpStatus.ACCEPTED, request.getStatusCode());

        TaskDto newTaskDto = new TaskDto(taskDto.getId(), Status.READY, "new name");
        Assertions.assertNotEquals(taskDto, newTaskDto);

        restTemplate.put(url, newTaskDto, TaskDto.class);
        ResponseEntity<TaskDto> response = restTemplate.getForEntity(url + "?id=" + taskDto.getId(), TaskDto.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotEquals(taskDto, response.getBody());
        Assertions.assertEquals(newTaskDto, response.getBody());
    }

    @Test
    void testTaskParams() {
        List<TaskParameterDto> list = new ArrayList<>();
        list.add(new TaskParameterDto("int", "value1", "1"));
        list.add(new TaskParameterDto("int", "value2", "2"));
        TaskDto taskDtoParam = new TaskDto(taskDto.getId(), taskDto.getStatus(), taskDto.getTaskName(), list);

        ResponseEntity<TaskDto> request = restTemplate.postForEntity(url, taskDtoParam, TaskDto.class);
        Assertions.assertEquals(HttpStatus.ACCEPTED, request.getStatusCode());

        ResponseEntity<TaskDto> response = restTemplate.getForEntity(url + "?id=" + taskDtoParam.getId(), TaskDto.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(taskDtoParam, response.getBody());
        Assertions.assertEquals(taskDtoParam.getTaskParameters(), response.getBody().getTaskParameters());
    }

    @Test
    void testTaskSubTask() {
        TaskDto subtask = new TaskDto(taskDto.getId() + 1, Status.READY, "subtask");

        TaskDto newTaskDto = new TaskDto(taskDto.getId(), taskDto.getStatus(), "new task dto", List.of(), List.of(subtask));
        ResponseEntity<TaskDto> request = restTemplate.postForEntity(url, newTaskDto, TaskDto.class);
        Assertions.assertEquals(HttpStatus.ACCEPTED, request.getStatusCode());

        // check subtask in db
        ResponseEntity<TaskDto> responseSubtask = restTemplate.getForEntity(url + "?id=" + subtask.getId(), TaskDto.class);
        Assertions.assertEquals(HttpStatus.OK, responseSubtask.getStatusCode());
        Assertions.assertEquals(subtask, responseSubtask.getBody());

        ResponseEntity<TaskDto> response = restTemplate.getForEntity(url + "?id=" + newTaskDto.getId(), TaskDto.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(newTaskDto.getSubTasks(), response.getBody().getSubTasks());
        Assertions.assertEquals(subtask, response.getBody().getSubTasks().get(0));
        Assertions.assertEquals(newTaskDto, response.getBody());
    }

    @Test
    void testTaskParamAndSubTask() {
        List<TaskParameterDto> list = new ArrayList<>();
        list.add(new TaskParameterDto("int", "value1", "1"));
        list.add(new TaskParameterDto("int", "value2", "2"));

        List<TaskDto> subtasks = new ArrayList<>();
        TaskDto subtask = new TaskDto(taskDto.getId() + 1, Status.READY, "");
        subtasks.add(subtask);

        TaskDto taskDtoParam = new TaskDto(taskDto.getId(), taskDto.getStatus(), taskDto.getTaskName(), list, subtasks);

        ResponseEntity<TaskDto> request = restTemplate.postForEntity(url, taskDtoParam, TaskDto.class);
        Assertions.assertEquals(HttpStatus.ACCEPTED, request.getStatusCode());

        ResponseEntity<TaskDto> response = restTemplate.getForEntity(url + "?id=" + taskDtoParam.getId(), TaskDto.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        Assertions.assertEquals(taskDtoParam, response.getBody());
        Assertions.assertEquals(taskDtoParam.getTaskParameters(), response.getBody().getTaskParameters());
        Assertions.assertEquals(taskDtoParam.getSubTasks(), response.getBody().getSubTasks());

        Assertions.assertEquals(subtasks, response.getBody().getSubTasks());
        Assertions.assertEquals(subtask, response.getBody().getSubTasks().get(0));
    }

    @Test
    void testTaskParamAndSubTaskParam() {
        List<TaskParameterDto> params = new ArrayList<>();
        params.add(new TaskParameterDto("int", "value1", "1"));
        params.add(new TaskParameterDto("int", "value2", "2"));

        List<TaskDto> subtasks = new ArrayList<>();
        TaskDto subtask = new TaskDto(taskDto.getId() + 1, Status.READY, "", params);
        subtasks.add(subtask);

        TaskDto taskDtoParam = new TaskDto(taskDto.getId(), taskDto.getStatus(), taskDto.getTaskName(), params, subtasks);
        // request
        ResponseEntity<TaskDto> request = restTemplate.postForEntity(url, taskDtoParam, TaskDto.class);
        Assertions.assertEquals(HttpStatus.ACCEPTED, request.getStatusCode());
        // response
        ResponseEntity<TaskDto> response = restTemplate.getForEntity(url + "?id=" + taskDtoParam.getId(), TaskDto.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        // check root task
        Assertions.assertEquals(taskDtoParam, response.getBody());
        Assertions.assertEquals(taskDtoParam.getTaskParameters(), response.getBody().getTaskParameters());
        Assertions.assertEquals(taskDtoParam.getSubTasks(), response.getBody().getSubTasks());
        // check subtask
        Assertions.assertEquals(subtasks, response.getBody().getSubTasks());
        Assertions.assertEquals(subtask, response.getBody().getSubTasks().get(0));
        Assertions.assertEquals(params, response.getBody().getSubTasks().get(0).getTaskParameters());
    }
}
