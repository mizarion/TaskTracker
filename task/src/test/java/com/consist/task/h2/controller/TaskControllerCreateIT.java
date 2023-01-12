package com.consist.task.h2.controller;

import com.consist.task.controller.TaskController;
import com.consist.task.h2.config.H2BaseTest;
import com.consist.task.model.Status;
import com.consist.task.model.dto.TaskDto;
import com.consist.task.model.dto.TaskParameterDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class TaskControllerCreateIT extends H2BaseTest {

    @Autowired
    private TaskController taskController;

    private TaskDto taskDto;
    private static final Integer FREE_ID = 999;

    private static final List<TaskParameterDto> params = new ArrayList<>();


    @BeforeEach
    void init() {
        taskDto = new TaskDto(FREE_ID, Status.READY, "test");
    }

    @AfterEach
    void delete() {
        taskController.deleteTask(taskDto.getId());
    }

    @Test
    void createTask() {
        taskController.postTask(taskDto);
        ResponseEntity<TaskDto> receivedTask = taskController.getTaskById(taskDto.getId());
        Assertions.assertEquals(taskDto, receivedTask.getBody());
    }

    @Test
    void createSubtask() {
        List<TaskDto> subtasks = new ArrayList<>();
        TaskDto subtask = new TaskDto(taskDto.getId() + 1, Status.READY, "", params);
        subtasks.add(subtask);


        TaskDto newTask = new TaskDto(taskDto.getId(), taskDto.getStatus(), taskDto.getTaskName(), params, subtasks);

        // create task with 1 subtask
        taskController.postTask(newTask);
        ResponseEntity<TaskDto> receivedTask = taskController.getTaskById(newTask.getId());

        Assertions.assertEquals(subtask, receivedTask.getBody().getSubTasks().get(0));
        Assertions.assertEquals(subtasks, receivedTask.getBody().getSubTasks());
        Assertions.assertEquals(newTask, receivedTask.getBody());
    }

    @Test
    void createSubsubtask() {

        List<TaskDto> subsubtasks = new ArrayList<>();
        TaskDto subsubtask = new TaskDto(taskDto.getId() + 2, Status.READY, "", params);
        subsubtasks.add(subsubtask);

        List<TaskDto> subtasks = new ArrayList<>();
        TaskDto subtask = new TaskDto(taskDto.getId() + 1, Status.READY, "", params, subsubtasks);
        subtasks.add(subtask);


        TaskDto newTask = new TaskDto(taskDto.getId(), taskDto.getStatus(), taskDto.getTaskName(), params, subtasks);

        // create task with 1 subtask
        taskController.postTask(newTask);
        ResponseEntity<TaskDto> receivedTask = taskController.getTaskById(newTask.getId());

        Assertions.assertEquals(subsubtask, receivedTask.getBody().getSubTasks().get(0).getSubTasks().get(0));
        Assertions.assertEquals(subsubtasks, receivedTask.getBody().getSubTasks().get(0).getSubTasks());

        Assertions.assertEquals(subtask, receivedTask.getBody().getSubTasks().get(0));
        Assertions.assertEquals(subtasks, receivedTask.getBody().getSubTasks());
        Assertions.assertEquals(newTask, receivedTask.getBody());
    }


    @Test
    void createParam() {
        List<TaskParameterDto> params = new ArrayList<>();
        params.add(new TaskParameterDto("1", "1", "1"));
        params.add(new TaskParameterDto("2", "2", "2"));

        TaskDto newTask = new TaskDto(taskDto.getId(), taskDto.getStatus(), taskDto.getTaskName(), params);

        // create task with 1 subtask
        taskController.postTask(newTask);
        ResponseEntity<TaskDto> receivedTask = taskController.getTaskById(newTask.getId());

        Assertions.assertEquals(params, receivedTask.getBody().getTaskParameters());
        Assertions.assertEquals(newTask, receivedTask.getBody());
    }


    @Test
    void createSubtaskParam() {
        List<TaskParameterDto> subparams = Arrays.asList(new TaskParameterDto("11", "11", "11"),
                new TaskParameterDto("22", "22", "22"));
        List<TaskDto> subtasks = Collections.singletonList(new TaskDto(taskDto.getId() + 1, Status.READY, "", subparams));
        List<TaskParameterDto> params = Arrays.asList(new TaskParameterDto("1", "1", "1"),
                new TaskParameterDto("2", "2", "2"));

        TaskDto newTask = new TaskDto(taskDto.getId(), taskDto.getStatus(), taskDto.getTaskName(), params, subtasks);

        taskController.postTask(newTask);
        ResponseEntity<TaskDto> receivedTask = taskController.getTaskById(newTask.getId());

        Assertions.assertEquals(subparams, receivedTask.getBody().getSubTasks().get(0).getTaskParameters());
        Assertions.assertEquals(params, receivedTask.getBody().getTaskParameters());
        Assertions.assertEquals(newTask, receivedTask.getBody());
    }
}
