package com.consist.task.unit.component;

import com.consist.task.component.TaskMapper;
import com.consist.task.component.TaskMapperImpl;
import com.consist.task.model.dto.TaskDto;
import com.consist.task.model.Status;
import com.consist.task.model.entity.TaskEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class TaskMapperTest {

    private TaskDto taskDto;
    private TaskDto taskDto2;
    private TaskDto taskDto3;
    private TaskEntity taskEntity;
    private TaskEntity taskEntity2;
    private TaskEntity taskEntity3;

    @BeforeEach
    void init() {
        taskDto = new TaskDto(1, Status.READY, "1");
        taskDto2 = new TaskDto(2, Status.READY, "2");
        taskDto3 = new TaskDto(3, Status.READY, "3");
        taskEntity = new TaskEntity(1, Status.READY, "1");
        taskEntity2 = new TaskEntity(2, Status.READY, "2");
        taskEntity3 = new TaskEntity(3, Status.READY, "3");
    }

    TaskMapper taskMapper = new TaskMapperImpl();

    @Test
    void mapToTaskEntity() {
        Assertions.assertEquals(taskEntity, taskMapper.mapToTaskEntity(taskDto));
    }

    @Test
    void mapToTaskDto() {
        Assertions.assertEquals(taskDto, taskMapper.mapToTaskDto(taskEntity));
    }

    @Test
    void mapToTaskEntityWithSubTask() {
        taskDto.createSubTask(taskDto2);
        Assertions.assertNotEquals(taskEntity, taskMapper.mapToTaskEntity(taskDto));
        taskEntity.createSubTask(taskEntity2);
        Assertions.assertEquals(taskEntity, taskMapper.mapToTaskEntity(taskDto));
    }

    @Test
    void mapToTaskEntityWithSubSubTask() {
        taskDto2.createSubTask(taskDto3);
        taskDto.createSubTask(taskDto2);
        Assertions.assertNotEquals(taskEntity, taskMapper.mapToTaskEntity(taskDto));
        taskEntity.createSubTask(taskEntity2);
        Assertions.assertNotEquals(taskEntity, taskMapper.mapToTaskEntity(taskDto));
        taskEntity2.createSubTask(taskEntity3);
        Assertions.assertEquals(taskEntity, taskMapper.mapToTaskEntity(taskDto));
    }

    @Test
    void mapToTaskDtoWithSubTask() {
        taskEntity.createSubTask(taskEntity2);
        Assertions.assertNotEquals(taskDto, taskMapper.mapToTaskDto(taskEntity));
        taskDto.createSubTask(taskDto2);
        Assertions.assertEquals(taskDto, taskMapper.mapToTaskDto(taskEntity));
    }

    @Test
    void mapToTaskDtoWithSubSubTask() {
        taskEntity2.createSubTask(taskEntity3);
        taskEntity.createSubTask(taskEntity2);
        Assertions.assertNotEquals(taskDto, taskMapper.mapToTaskDto(taskEntity));
        taskDto.createSubTask(taskDto2);
        Assertions.assertNotEquals(taskDto, taskMapper.mapToTaskDto(taskEntity));
        taskDto2.createSubTask(taskDto3);
        Assertions.assertEquals(taskDto, taskMapper.mapToTaskDto(taskEntity));
    }
}