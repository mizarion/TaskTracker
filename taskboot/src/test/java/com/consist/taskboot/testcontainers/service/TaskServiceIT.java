package com.consist.taskboot.testcontainers.service;

import com.consist.taskboot.model.Status;
import com.consist.taskboot.model.dto.TaskDto;
import com.consist.taskboot.model.entity.TaskEntity;
import com.consist.taskboot.service.TaskService;
import com.consist.taskboot.testcontainers.config.PostgresBaseIT;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TaskServiceIT extends PostgresBaseIT {

    @Autowired
    private TaskService taskService;
    private final TaskEntity taskEntity1 = new TaskEntity(1, Status.READY, "task1");
    private final TaskDto taskDto1 = new TaskDto(1, Status.READY, "task1");
    private final TaskEntity task2 = new TaskEntity(2, Status.READY, "task2");
    private final TaskEntity testTaskEntity = new TaskEntity(3, Status.READY, "task3");
    private final TaskDto testTaskDto = new TaskDto(3, Status.READY, "task3");

    @BeforeEach
    void init() {
        taskService.create(taskEntity1);
        taskService.create(task2);
    }

    @AfterEach
    void close() {
        taskService.deleteById(taskEntity1.getId());
        taskService.deleteById(task2.getId());
    }

    @Test
    void findById() {
        Assertions.assertEquals(taskDto1, taskService.findById(taskDto1.getId()));
    }

    @Test
    void testSaveAndDelete() {
        taskService.create(testTaskEntity);
        Assertions.assertEquals(testTaskDto, taskService.findById(testTaskEntity.getId()));
        taskService.deleteById(testTaskEntity.getId());
    }

    @Test
    void update() {
        taskService.create(testTaskEntity);
        TaskDto receivedTask = taskService.findById(testTaskEntity.getId());
        Assertions.assertEquals(testTaskDto, receivedTask);

        taskService.update(new TaskEntity(testTaskEntity.getId(), testTaskEntity.getStatus(), "new name"));
        TaskDto receivedTask2 = taskService.findById(testTaskEntity.getId());
        Assertions.assertNotEquals(receivedTask, receivedTask2);

        taskService.deleteById(testTaskEntity.getId());
    }
}
