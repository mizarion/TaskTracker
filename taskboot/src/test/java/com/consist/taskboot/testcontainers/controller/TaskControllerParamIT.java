package com.consist.taskboot.testcontainers.controller;

import com.consist.taskboot.controller.TaskController;
import com.consist.taskboot.model.Status;
import com.consist.taskboot.model.dto.TaskDto;
import com.consist.taskboot.model.dto.TaskParameterDto;
import com.consist.taskboot.testcontainers.config.PostgresBaseIT;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("DataFlowIssue")
class TaskControllerParamIT extends PostgresBaseIT {

    @Autowired
    private TaskController taskController;

    private TaskDto taskDto;
    private static final Integer FREE_ID = 999;

    @BeforeEach
    void init() {
        taskDto = new TaskDto(FREE_ID, Status.READY, "test");
    }

    @AfterEach
    void delete() {
        taskController.deleteTask(taskDto.getId());
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
        ResponseEntity<Void> request = taskController.postTask(taskDtoParam);
        Assertions.assertEquals(HttpStatus.ACCEPTED, request.getStatusCode());
        // response
        ResponseEntity<TaskDto> response = taskController.getTaskById(taskDtoParam.getId());
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

    @Test
    void updateParamAndSubtask() {
        List<TaskParameterDto> params = new ArrayList<>();
        params.add(new TaskParameterDto("int", "value1", "1"));
        params.add(new TaskParameterDto("int", "value2", "2"));

        List<TaskDto> subtasks = new ArrayList<>();
        TaskDto subtask = new TaskDto(taskDto.getId() + 1, Status.READY, "", params);
        subtasks.add(subtask);

        // post
        ResponseEntity<Void> post = taskController.postTask(taskDto);
        Assertions.assertEquals(HttpStatus.ACCEPTED, post.getStatusCode());
        // get
        ResponseEntity<TaskDto> get = taskController.getTaskById(taskDto.getId());
        Assertions.assertEquals(HttpStatus.OK, get.getStatusCode());
        // check
        Assertions.assertTrue(get.getBody().getSubTasks().isEmpty());
        Assertions.assertTrue(get.getBody().getTaskParameters().isEmpty());

        // update
        TaskDto taskDtoParam = new TaskDto(taskDto.getId(), taskDto.getStatus(), taskDto.getTaskName(), params, subtasks);
        ResponseEntity<Void> update = taskController.updateTask(taskDtoParam);
        Assertions.assertEquals(HttpStatus.ACCEPTED, update.getStatusCode());
        // get updated
        ResponseEntity<TaskDto> getUpdated = taskController.getTaskById(taskDtoParam.getId());
        Assertions.assertEquals(HttpStatus.OK, getUpdated.getStatusCode());

        // check root task
        Assertions.assertEquals(taskDtoParam.getTaskParameters(), getUpdated.getBody().getTaskParameters());
        Assertions.assertEquals(taskDtoParam, getUpdated.getBody());
        Assertions.assertEquals(taskDtoParam.getTaskParameters(), getUpdated.getBody().getTaskParameters());
        // check subtask
        Assertions.assertFalse(getUpdated.getBody().getSubTasks().isEmpty());
        Assertions.assertEquals(subtasks, getUpdated.getBody().getSubTasks());
        Assertions.assertEquals(params, getUpdated.getBody().getSubTasks().get(0).getTaskParameters());
    }

    @Test
    void updateParamAndSubtask3() {
        List<TaskParameterDto> params = new ArrayList<>();
        params.add(new TaskParameterDto("int", "value1", "1"));
        params.add(new TaskParameterDto("int", "value2", "2"));

        List<TaskDto> subtasks = new ArrayList<>();
        TaskDto subtask = new TaskDto(taskDto.getId() + 1, Status.READY, "", params);
        subtasks.add(subtask);
        TaskDto newTask = new TaskDto(taskDto.getId(), taskDto.getStatus(), taskDto.getTaskName(), params, subtasks);

        // create task with 1 subtask
        Assertions.assertEquals(HttpStatus.ACCEPTED, taskController.postTask(newTask).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK, taskController.getTaskById(newTask.getId()).getStatusCode());
        // update subtask
        List<TaskDto> subsubtasks = new ArrayList<>();
        TaskDto subsubtask = new TaskDto(subtask.getId() + 1, Status.READY, "", params);
        subsubtasks.add(subsubtask);
        TaskDto newSubtask = new TaskDto(subtask.getId(), subtask.getStatus(), subtask.getTaskName(), params, subsubtasks);
        ResponseEntity<Void> update = taskController.updateTask(newSubtask);
        Assertions.assertEquals(HttpStatus.ACCEPTED, update.getStatusCode());
        // get & check updated subtask
        ResponseEntity<TaskDto> getUpdatedSubtask = taskController.getTaskById(newSubtask.getId());
        Assertions.assertEquals(HttpStatus.OK, getUpdatedSubtask.getStatusCode());
        Assertions.assertEquals(newSubtask, getUpdatedSubtask.getBody());

        // get new root
        List<TaskDto> rootSubtask = new ArrayList<>();
        rootSubtask.add(newSubtask);
        TaskDto newRootExpected = new TaskDto(newTask.getId(), newTask.getStatus(), newTask.getTaskName(),
                params, rootSubtask);

        ResponseEntity<TaskDto> newRootReceived = taskController.getTaskById(taskDto.getId());

        // check root task
        Assertions.assertEquals(newRootExpected, newRootReceived.getBody());
        Assertions.assertEquals(rootSubtask, newRootReceived.getBody().getSubTasks());
        Assertions.assertEquals(subsubtasks, newRootReceived.getBody().getSubTasks().get(0).getSubTasks());
    }

    @Test
    void updateTaskParam() {
        TaskParameterDto parameterDto = new TaskParameterDto("int", "value1", "1");
        TaskParameterDto parameterDto2 = new TaskParameterDto("int", "value2", "3");

        List<TaskParameterDto> params = new ArrayList<>();
        params.add(parameterDto);
        params.add(parameterDto2);

        TaskDto taskDtoParam = new TaskDto(taskDto.getId(), taskDto.getStatus(), taskDto.getTaskName(), params);

        Assertions.assertEquals(HttpStatus.ACCEPTED, taskController.postTask(taskDtoParam).getStatusCode());
        ResponseEntity<TaskDto> source = taskController.getTaskById(taskDtoParam.getId());
        Assertions.assertEquals(HttpStatus.OK, source.getStatusCode());
        Assertions.assertEquals(params, source.getBody().getTaskParameters());
        // check deletions
        List<TaskParameterDto> emptyParams = new ArrayList<>();
        TaskDto emptyTask = new TaskDto(taskDto.getId(), taskDto.getStatus(), taskDto.getTaskName(), emptyParams);
        Assertions.assertEquals(HttpStatus.ACCEPTED, taskController.updateTask(emptyTask).getStatusCode());
        ResponseEntity<TaskDto> emptyUpdate = taskController.getTaskById(taskDtoParam.getId());
        Assertions.assertEquals(emptyTask.getTaskParameters(), emptyUpdate.getBody().getTaskParameters());
        // check insertions
        List<TaskParameterDto> newParams = new ArrayList<>();
        newParams.add(new TaskParameterDto("int", "name3", "5"));
        newParams.add(parameterDto);
        TaskDto newTask = new TaskDto(taskDto.getId(), taskDto.getStatus(), taskDto.getTaskName(), newParams);
        Assertions.assertEquals(HttpStatus.ACCEPTED, taskController.updateTask(newTask).getStatusCode());
        ResponseEntity<TaskDto> updated = taskController.getTaskById(newTask.getId());
        Assertions.assertEquals(newParams, updated.getBody().getTaskParameters());
        // check updates
        newParams.remove(parameterDto);
        newParams.add(new TaskParameterDto(parameterDto.getType(), "new name5", "100"));
        TaskDto updateTask = new TaskDto(taskDto.getId(), taskDto.getStatus(), taskDto.getTaskName(), newParams);
        Assertions.assertEquals(HttpStatus.ACCEPTED, taskController.updateTask(updateTask).getStatusCode());
        ResponseEntity<TaskDto> updated2 = taskController.getTaskById(updateTask.getId());
        Assertions.assertEquals(newParams, updated2.getBody().getTaskParameters());
    }

    @Test
    void updateSubTaskParam() {
        List<TaskParameterDto> params = new ArrayList<>();
        params.add(new TaskParameterDto("int", "value1", "1"));
        params.add(new TaskParameterDto("int", "value2", "2"));

        List<TaskDto> subtasks = new ArrayList<>();
        TaskDto subtask = new TaskDto(taskDto.getId() + 1, Status.READY, "", params);
        subtasks.add(subtask);

        TaskDto taskDtoParam = new TaskDto(taskDto.getId(), taskDto.getStatus(), taskDto.getTaskName(), params, subtasks);
        Assertions.assertEquals(HttpStatus.ACCEPTED, taskController.postTask(taskDtoParam).getStatusCode());
        ResponseEntity<TaskDto> response = taskController.getTaskById(taskDtoParam.getId());
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(params, response.getBody().getSubTasks().get(0).getTaskParameters());
        // check deletions
        List<TaskParameterDto> emptyParams = new ArrayList<>();
        List<TaskDto> emptySubtasks = new ArrayList<>();
        TaskDto newSubtask = new TaskDto(subtask.getId(), subtask.getStatus(), subtask.getTaskName(), emptyParams);
        emptySubtasks.add(newSubtask);
        TaskDto emptyTask = new TaskDto(taskDto.getId(), taskDto.getStatus(), taskDto.getTaskName(), params, emptySubtasks);
        Assertions.assertEquals(HttpStatus.ACCEPTED, taskController.updateTask(emptyTask).getStatusCode());
        ResponseEntity<TaskDto> emptyUpdate = taskController.getTaskById(taskDtoParam.getId());
        Assertions.assertEquals(params, emptyUpdate.getBody().getTaskParameters());
        Assertions.assertEquals(emptyParams, emptyUpdate.getBody().getSubTasks().get(0).getTaskParameters());
    }
}
