package com.consist.task.resttemplate;

import com.consist.task.model.Status;
import com.consist.task.model.dto.TaskDto;
import com.consist.task.model.dto.TaskParameterDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RestTemplateConfig.class, loader = AnnotationConfigContextLoader.class)
@TestExecutionListeners(inheritListeners = false, listeners =
        {DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class})
class RestControllerLive {

    private final RestTemplate restTemplate;
    private final String url;

    private TaskDto taskDto;
    public static final Integer FREE_ID = 999;

    public RestControllerLive(@Autowired URL url, @Autowired RestTemplate restTemplate) {
        this.url = url.toString();
        this.restTemplate = restTemplate;
    }

    @BeforeEach
    void init() {
        taskDto = new TaskDto(FREE_ID, Status.READY, "test");
    }

    @AfterEach
    void delete() {
        restTemplate.delete(url + "?id=" + taskDto.getId());
    }

    @Test
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
        TaskDto subtask = new TaskDto(taskDto.getId() + 1, Status.READY, "");
        taskDto.createSubTask(subtask);

        ResponseEntity<TaskDto> request = restTemplate.postForEntity(url, taskDto, TaskDto.class);
        Assertions.assertEquals(HttpStatus.ACCEPTED, request.getStatusCode());

        // check subtask in db
        ResponseEntity<TaskDto> responseSubtask = restTemplate.getForEntity(url + "?id=" + subtask.getId(), TaskDto.class);
        Assertions.assertEquals(HttpStatus.OK, responseSubtask.getStatusCode());
        Assertions.assertEquals(subtask, responseSubtask.getBody());

        ResponseEntity<TaskDto> response = restTemplate.getForEntity(url + "?id=" + taskDto.getId(), TaskDto.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(taskDto, response.getBody());
        Assertions.assertEquals(taskDto.getSubTasks(), response.getBody().getSubTasks());
        Assertions.assertEquals(subtask, response.getBody().getSubTasks().get(0));
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
