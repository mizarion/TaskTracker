package com.consist.task.unit.model.dto;

import com.consist.task.model.Status;
import com.consist.task.model.dto.TaskDto;
import com.consist.task.model.dto.TaskParameterDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class TaskDtoTest {

    @Test
    void testEquals() {
        TaskDto task = new TaskDto(1, Status.READY, "");
        Assertions.assertEquals(task, task);
    }

    @Test
    void testNewEquals() {
        Assertions.assertEquals(
                new TaskDto(1, Status.READY, ""),
                new TaskDto(1, Status.READY, ""));
    }

    @Test
    void testNotEqualsId() {
        Assertions.assertNotEquals(
                new TaskDto(1, Status.READY, ""),
                new TaskDto(2, Status.READY, ""));
    }

    @Test
    void testNotEqualsStatus() {
        Assertions.assertNotEquals(
                new TaskDto(1, Status.READY, ""),
                new TaskDto(1, Status.FINISHED, ""));
    }

    @Test
    void testNotEqualsName() {
        Assertions.assertNotEquals(
                new TaskDto(1, Status.READY, "A"),
                new TaskDto(1, Status.READY, "B"));
    }

    @Test
    void testEqualsTaskParameters() {
        TaskParameterDto parameterDto = new TaskParameterDto("", "", "");
        List<TaskParameterDto> taskParameterDto = new ArrayList<>();
        taskParameterDto.add(parameterDto);

        TaskParameterDto parameterDto2 = new TaskParameterDto("", "", "");
        List<TaskParameterDto> taskParameterDto2 = new ArrayList<>();
        taskParameterDto2.add(parameterDto2);

        Assertions.assertEquals(
                new TaskDto(1, Status.READY, "", taskParameterDto),
                new TaskDto(1, Status.READY, "", taskParameterDto2));
    }

    @Test
    void testNotEqualsTaskParameters() {
        TaskParameterDto parameterDto = new TaskParameterDto("", "", "");
        List<TaskParameterDto> taskParameterDto = new ArrayList<>();
        taskParameterDto.add(parameterDto);
        Assertions.assertNotEquals(
                new TaskDto(1, Status.READY, "", taskParameterDto),
                new TaskDto(1, Status.READY, ""));
    }
}