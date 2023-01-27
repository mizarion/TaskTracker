package com.consist.taskboot.testcontainers.controller;

import com.consist.taskboot.controller.TaskController;
import com.consist.taskboot.model.Status;
import com.consist.taskboot.model.dto.TaskDto;
import com.consist.taskboot.model.dto.TaskParameterDto;
import com.consist.taskboot.testcontainers.config.PostgresBaseIT;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

class TaskControllerDeleteIT extends PostgresBaseIT {

    @Autowired
    private TaskController taskController;
    private static final TaskDto taskDto = new TaskDto(999, Status.READY, "test");

    private static final List<TaskParameterDto> params = new ArrayList<>();

    @Test
    @Order(1)
    void deleteNonExistedTask() {
        Assertions.assertThrows(Exception.class, () -> taskController.deleteTask(taskDto.getId()));
    }

    @Test
    @Order(2)
    void deleteTask() {
        taskController.postTask(taskDto);
        Assertions.assertEquals(taskDto, taskController.getTaskById(taskDto.getId()).getBody());
        Assertions.assertEquals(HttpStatus.ACCEPTED, taskController.deleteTask(taskDto.getId()).getStatusCode());
        Assertions.assertThrows(Exception.class, () -> taskController.deleteTask(taskDto.getId()));
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
        Assertions.assertEquals(HttpStatus.ACCEPTED, taskController.deleteTask(taskDto.getId()).getStatusCode());
        Assertions.assertEquals(newTask, receivedTask.getBody());
        Assertions.assertThrows(Exception.class, () -> taskController.deleteTask(taskDto.getId()));
    }
}
