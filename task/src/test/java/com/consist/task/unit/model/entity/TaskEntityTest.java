package com.consist.task.unit.model.entity;

import com.consist.task.model.Status;
import com.consist.task.model.entity.TaskEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TaskEntityTest {

    @Test
    void testEquals() {
        TaskEntity task = new TaskEntity(1, Status.READY, "");
        Assertions.assertEquals(task, task);
    }

    @Test
    void testNewEquals() {
        Assertions.assertEquals(
                new TaskEntity(1, Status.READY, ""),
                new TaskEntity(1, Status.READY, ""));
    }

    @Test
    void testNotEqualId() {
        Assertions.assertNotEquals(
                new TaskEntity(1, Status.READY, ""),
                new TaskEntity(2, Status.READY, ""));
    }

    @Test
    void testNotEqualStatus() {
        Assertions.assertNotEquals(
                new TaskEntity(1, Status.READY, ""),
                new TaskEntity(1, Status.FINISHED, ""));
    }

    @Test
    void testNotEqualName() {
        Assertions.assertNotEquals(
                new TaskEntity(1, Status.READY, "A"),
                new TaskEntity(1, Status.READY, "B"));
    }
}