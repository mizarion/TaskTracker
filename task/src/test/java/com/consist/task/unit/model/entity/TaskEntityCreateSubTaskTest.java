package com.consist.task.unit.model.entity;

import com.consist.task.model.Status;
import com.consist.task.model.entity.TaskEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TaskEntityCreateSubTaskTest {

    private TaskEntity taskEntity;
    private TaskEntity taskEntity2;
    private TaskEntity taskEntity3;

    @BeforeEach
    void init() {
        taskEntity = new TaskEntity(1, Status.READY, "1");
        taskEntity2 = new TaskEntity(2, Status.READY, "2");
        taskEntity3 = new TaskEntity(3, Status.READY, "3");
    }

    @Test
    void createSubTasks() {
        Assertions.assertEquals(0, taskEntity.getSubTasks().size());
        taskEntity.createSubTask(taskEntity2);
        Assertions.assertEquals(1, taskEntity.getSubTasks().size());
        Assertions.assertEquals(taskEntity2, taskEntity.getSubTasks().get(0));
    }

    @Test
    void createSubSubTasks() {
        Assertions.assertEquals(0, taskEntity.getSubTasks().size());
        Assertions.assertEquals(0, taskEntity2.getSubTasks().size());
        taskEntity.createSubTask(taskEntity2);
        taskEntity2.createSubTask(taskEntity3);
        Assertions.assertEquals(1, taskEntity.getSubTasks().size());
        Assertions.assertEquals(taskEntity2, taskEntity.getSubTasks().get(0));
        Assertions.assertEquals(1, taskEntity2.getSubTasks().size());
        Assertions.assertEquals(taskEntity3, taskEntity2.getSubTasks().get(0));
    }


    @Test
    void createLoopSubTasks() {
        Assertions.assertEquals(0, taskEntity.getSubTasks().size());
        // когда нет лямбды :(
        boolean flag = false;
        try {
            taskEntity.createSubTask(taskEntity);
        } catch (Exception ex) {
            flag = true;
        }
        Assertions.assertEquals(true, flag);
        Assertions.assertEquals(0, taskEntity.getSubTasks().size());
    }
}