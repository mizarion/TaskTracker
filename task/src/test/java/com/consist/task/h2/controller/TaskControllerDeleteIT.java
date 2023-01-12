package com.consist.task.h2.controller;

import com.consist.task.controller.TaskController;
import com.consist.task.h2.config.H2BaseTest;
import com.consist.task.model.Status;
import com.consist.task.model.dto.TaskDto;
import com.consist.task.model.dto.TaskParameterDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TaskControllerDeleteIT extends H2BaseTest {

    @Autowired
    private TaskController taskController;

    private static final TaskDto taskDto = new TaskDto(999, Status.READY, "test");

    private static final List<TaskParameterDto> params = new ArrayList<>();


    private void check() {
        boolean flag = false;
        try {
            taskController.deleteTask(taskDto.getId());
        } catch (Exception e) {
            flag = true;
        }
        Assertions.assertEquals(true, flag);
    }

    @Test
    @Order(1)
    void deleteNonExistedTask() {
        check();
    }


    @Test
    @Order(2)
    void deleteTask() {
        taskController.postTask(taskDto);
        Assertions.assertEquals(taskDto, taskController.getTaskById(taskDto.getId()).getBody());
        Assertions.assertEquals(HttpStatus.ACCEPTED, taskController.deleteTask(taskDto.getId()).getStatusCode());
        check();
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

        Assertions.assertEquals(newTask, receivedTask.getBody());


        Assertions.assertEquals(HttpStatus.ACCEPTED, taskController.deleteTask(taskDto.getId()).getStatusCode());

        check();
    }
}
