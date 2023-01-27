package com.consist.taskboot.mock.controller;

import com.consist.taskboot.controller.TaskController;
import com.consist.taskboot.model.Status;
import com.consist.taskboot.model.dto.TaskDto;
import com.consist.taskboot.model.entity.TaskEntity;
import com.consist.taskboot.service.TaskService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest
class TaskControllerTest {
    @Autowired
    private TaskController taskController;

    @MockBean
    private TaskService taskServiceMock;

    private final TaskDto taskDto = new TaskDto(1, Status.READY, "task1");
    private final TaskDto taskDtoUpdate = new TaskDto(1, Status.READY, "new task1");
    private final TaskEntity taskEntityUpdate = new TaskEntity(1, Status.READY, "new task1");
    private final TaskEntity taskEntity = new TaskEntity(1, Status.READY, "task1");

    @Test
    void getTask() {
        // Mock
        Mockito.when(taskServiceMock.findById(taskDto.getId())).thenReturn(taskDto);

        ResponseEntity<TaskDto> response = taskController.getTaskById(taskDto.getId());
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(taskDto, response.getBody());
    }

    @Test
    void postTask() {
        // Mock
        Mockito.doNothing().when(taskServiceMock).create(taskEntity);
        // Controller post
        ResponseEntity<Void> responsePost = taskController.postTask(taskDto);
        Assertions.assertEquals(HttpStatus.ACCEPTED, responsePost.getStatusCode());
    }

    @Test
    void update() {
        // Mock
        Mockito.when(taskServiceMock.findById(taskDto.getId())).thenReturn(taskDto, taskDtoUpdate);
        Mockito.doNothing().when(taskServiceMock).update(taskEntityUpdate);
        // Get before update
        ResponseEntity<TaskDto> response = taskController.getTaskById(taskDto.getId());
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(taskDto, response.getBody());
        // Update
        ResponseEntity<Void> responseUpdate = taskController.updateTask(taskDtoUpdate);
        Assertions.assertEquals(HttpStatus.ACCEPTED, responseUpdate.getStatusCode());
        // Get after update
        ResponseEntity<TaskDto> getUpdated = taskController.getTaskById(taskDto.getId());
        Assertions.assertEquals(HttpStatus.OK, getUpdated.getStatusCode());
        Assertions.assertNotEquals(taskDto, getUpdated.getBody());
        Assertions.assertEquals(taskDtoUpdate, getUpdated.getBody());
    }

    @Test
    void delete() {
        Mockito.doNothing().when(taskServiceMock).deleteById(taskDto.getId());

        ResponseEntity<Void> response = taskController.deleteTask(taskDto.getId());
        Assertions.assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    void deleteTwice() {
        // Mock
        Mockito.doNothing().when(taskServiceMock).deleteById(taskDto.getId());
        // Delete
        ResponseEntity<Void> response = taskController.deleteTask(taskDto.getId());
        Assertions.assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        // Mock
        Mockito.doThrow(IllegalArgumentException.class).when(taskServiceMock).deleteById(taskDto.getId());
        // Delete again
        Assertions.assertThrows(Exception.class, () -> taskController.deleteTask(taskDto.getId()));
    }

    @Test
    void deleteAndGet() {
        // Mock
        Mockito.doNothing().when(taskServiceMock).deleteById(taskDto.getId());
        Mockito.when(taskServiceMock.findById(taskDto.getId())).thenReturn(taskDto).thenThrow(new IllegalArgumentException());
        // Get before deletion
        ResponseEntity<TaskDto> response = taskController.getTaskById(taskDto.getId());
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(taskDto, response.getBody());
        // Delete
        ResponseEntity<Void> responseUpdate = taskController.deleteTask(taskDto.getId());
        Assertions.assertEquals(HttpStatus.ACCEPTED, responseUpdate.getStatusCode());
        // Get after deletion
        Assertions.assertThrows(Exception.class, () -> taskController.getTaskById(taskDto.getId()));
    }
}