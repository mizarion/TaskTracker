package com.consist.task.h2.repository;

import com.consist.task.h2.config.H2BaseTest;
import com.consist.task.model.Status;
import com.consist.task.model.entity.TaskEntity;
import com.consist.task.repository.TaskRepository;
import com.consist.task.repository.specification.TaskSpecificationById;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

@Sql({"classpath:schema.sql"})
class TaskRepositoryIT extends H2BaseTest {

    @Autowired
    private TaskRepository taskRepository;
    private final TaskEntity task1 = new TaskEntity(1, Status.READY, "task1");
    private final TaskEntity task2 = new TaskEntity(2, Status.READY, "task2");
    private final TaskEntity testTask = new TaskEntity(3, Status.READY, "task3");

    @BeforeEach
    void init() {
        taskRepository.create(task1);
        taskRepository.create(task2);
    }

    @AfterEach
    void close() {
        taskRepository.deleteById(task1.getId());
        taskRepository.deleteById(task2.getId());
    }

    @Test
    void queryTaskSpecificationById() {
        Assertions.assertEquals(task1, taskRepository.query(new TaskSpecificationById(task1.getId())).get(0));
    }

    @Test
    void testSaveAndDelete() {
        taskRepository.create(testTask);
        Assertions.assertEquals(testTask, taskRepository.query(new TaskSpecificationById(testTask.getId())).get(0));
        taskRepository.deleteById(testTask.getId());
    }

    @Test
    void update() {
        taskRepository.create(testTask);
        TaskEntity receivedTask = taskRepository.query(new TaskSpecificationById(testTask.getId())).get(0);
        Assertions.assertEquals(testTask, receivedTask);
        TaskEntity newTask = new TaskEntity(testTask.getId(), testTask.getStatus(), "new name");
        taskRepository.update(newTask);
        TaskEntity receivedTask2 = taskRepository.query(new TaskSpecificationById(testTask.getId())).get(0);
        Assertions.assertNotEquals(receivedTask, receivedTask2);
        Assertions.assertEquals(newTask, receivedTask2);
        taskRepository.deleteById(testTask.getId());
    }
}
