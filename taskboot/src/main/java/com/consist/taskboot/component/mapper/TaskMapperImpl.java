package com.consist.taskboot.component.mapper;

import com.consist.taskboot.model.Status;
import com.consist.taskboot.model.dto.TaskDto;
import com.consist.taskboot.model.dto.TaskParameterDto;
import com.consist.taskboot.model.entity.TaskEntity;
import com.consist.taskboot.model.entity.TaskParameter;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class TaskMapperImpl implements TaskMapper {
    @Override
    public TaskEntity mapToTaskEntity(TaskDto task) {
        List<TaskEntity> subtasks = task.getSubTasks().stream()
                .map(this::mapToTaskEntity)
                .toList();
        List<TaskParameter> parameters = task.getTaskParameters().stream()
                .map(p -> new TaskParameter(p.paramType(), p.paramName(), p.paramValue(), task.getId()))
                .toList();
        return new TaskEntity(task.getId(), task.getStatus(), task.getTaskName(), parameters, subtasks);
    }

    @Override
    public TaskDto mapToTaskDto(TaskEntity task) {
        List<TaskDto> subtasks = task.getSubTasks().stream()
                .map(this::mapToTaskDto)
                .toList();
        List<TaskParameterDto> parameters = task.getTaskParameters().stream()
                .map(p -> new TaskParameterDto(p.type(), p.taskName(), p.value()))
                .toList();
        return new TaskDto(task.getId(), task.getStatus(), task.getTaskName(), parameters, subtasks);
    }

    @Override
    public TaskEntity mapRow(ResultSet row, int i) throws SQLException {
        return new TaskEntity(
                row.getInt("task_id"),
                Status.valueOf(row.getString("status")),
                row.getString("name"),
                row.getInt("parent_id")
        );
    }
}
