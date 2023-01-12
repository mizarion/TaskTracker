package com.consist.task.h2.controller;

import com.consist.task.controller.TaskController;
import com.consist.task.h2.config.H2BaseTest;
import com.consist.task.model.Status;
import com.consist.task.model.dto.TaskDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

@Sql({"classpath:schema.sql"})
class TaskControllerIT extends H2BaseTest {

    @Autowired
    private TaskController taskController;

    private final TaskDto task1 = new TaskDto(1, Status.READY, "task1");
    private final TaskDto task2 = new TaskDto(2, Status.READY, "task2");
    private final TaskDto testTask = new TaskDto(3, Status.READY, "task3");

    @BeforeEach
    void init() {
        taskController.postTask(task1);
        taskController.postTask(task2);
    }

    @AfterEach
    void close() {
        taskController.deleteTask(task1.getId());
        taskController.deleteTask(task2.getId());
    }

    @Test
    void getTask() {
        ResponseEntity<TaskDto> response = taskController.getTaskById(task1.getId());
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(task1, response.getBody());

        ResponseEntity<TaskDto> response2 = taskController.getTaskById(task2.getId());
        Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
        Assertions.assertEquals(task2, response2.getBody());

        Assertions.assertNotEquals(response, response2);
    }

    @Test
    void postAndDeleteTask() {
        ResponseEntity<Void> responsePost = taskController.postTask(testTask);
        Assertions.assertEquals(HttpStatus.ACCEPTED, responsePost.getStatusCode());

        ResponseEntity<TaskDto> responseGet = taskController.getTaskById(testTask.getId());
        Assertions.assertEquals(HttpStatus.OK, responseGet.getStatusCode());
        Assertions.assertEquals(testTask, responseGet.getBody());

        ResponseEntity<Void> responseDelete = taskController.deleteTask(testTask.getId());
        Assertions.assertEquals(HttpStatus.ACCEPTED, responseDelete.getStatusCode());
    }

    @Test
    void update() {
        ResponseEntity<Void> responsePost = taskController.postTask(testTask);
        Assertions.assertEquals(HttpStatus.ACCEPTED, responsePost.getStatusCode());

        ResponseEntity<TaskDto> responseGet1 = taskController.getTaskById(testTask.getId());
        Assertions.assertEquals(HttpStatus.OK, responseGet1.getStatusCode());
        Assertions.assertEquals(testTask, responseGet1.getBody());

        TaskDto newTask = new TaskDto(testTask.getId(), testTask.getStatus(), "new name");
        ResponseEntity<Void> responseUpdate = taskController.updateTask(newTask);
        Assertions.assertEquals(HttpStatus.ACCEPTED, responseUpdate.getStatusCode());

        ResponseEntity<TaskDto> responseGet2 = taskController.getTaskById(testTask.getId());
        Assertions.assertEquals(HttpStatus.OK, responseGet2.getStatusCode());
        Assertions.assertNotEquals(testTask, responseGet2.getBody());
        Assertions.assertEquals(newTask, responseGet2.getBody());

        ResponseEntity<Void> responseDelete = taskController.deleteTask(testTask.getId());
        Assertions.assertEquals(HttpStatus.ACCEPTED, responseDelete.getStatusCode());
    }
}