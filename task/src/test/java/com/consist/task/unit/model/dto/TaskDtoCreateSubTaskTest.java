package com.consist.task.unit.model.dto;

import com.consist.task.model.Status;
import com.consist.task.model.dto.TaskDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TaskDtoCreateSubTaskTest {

    private TaskDto taskDto;
    private TaskDto taskDto2;
    private TaskDto taskDto3;

    @BeforeEach
    void init() {
        taskDto = new TaskDto(1, Status.READY, "1");
        taskDto2 = new TaskDto(2, Status.READY, "2");
        taskDto3 = new TaskDto(3, Status.READY, "3");
    }

    @Test
    void createSubTasks() {
        taskDto.createSubTask(taskDto2);
        Assertions.assertEquals(1, taskDto.getSubTasks().size());
        Assertions.assertEquals(taskDto2, taskDto.getSubTasks().get(0));
    }

    @Test
    void createSubSubTasks() {
        Assertions.assertEquals(0, taskDto.getSubTasks().size());
        Assertions.assertEquals(0, taskDto2.getSubTasks().size());
        taskDto.createSubTask(taskDto2);
        taskDto2.createSubTask(taskDto3);
        Assertions.assertEquals(1, taskDto.getSubTasks().size());
        Assertions.assertEquals(taskDto2, taskDto.getSubTasks().get(0));
        Assertions.assertEquals(1, taskDto2.getSubTasks().size());
        Assertions.assertEquals(taskDto3, taskDto2.getSubTasks().get(0));
    }


    @Test
    void createLoopSubTasks() {
        Assertions.assertEquals(0, taskDto.getSubTasks().size());
        // когда нет лямбды :(
        boolean flag = false;
        try {
            taskDto.createSubTask(taskDto);
        } catch (Exception ex) {
            flag = true;
        }
        Assertions.assertEquals(true, flag);
        Assertions.assertEquals(0, taskDto.getSubTasks().size());
    }
}
