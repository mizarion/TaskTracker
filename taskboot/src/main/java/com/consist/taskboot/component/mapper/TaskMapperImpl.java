package com.consist.taskboot.component.mapper;

import com.consist.taskboot.model.Status;
import com.consist.taskboot.model.dto.TaskDto;
import com.consist.taskboot.model.dto.TaskParameterDto;
import com.consist.taskboot.model.entity.TaskEntity;
import com.consist.taskboot.model.entity.TaskParameter;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class TaskMapperImpl implements TaskMapper {
    @Override
    public TaskEntity mapToTaskEntity(TaskDto task) {
        List<TaskEntity> subtasks = new ArrayList<>();
        List<TaskParameter> parameters = new ArrayList<>();
        for (TaskDto subtask : task.getSubTasks()) {
            subtasks.add(mapToTaskEntity(subtask));
        }
        for (TaskParameterDto parameter : task.getTaskParameters()) {
            parameters.add(mapToTaskParameter(parameter));
        }
        return new TaskEntity(task.getId(), task.getStatus(), task.getTaskName(), parameters, subtasks);
    }

    @Override
    public TaskDto mapToTaskDto(TaskEntity task) {
        List<TaskDto> subtasks = new ArrayList<>();
        List<TaskParameterDto> parameters = new ArrayList<>();
        for (TaskEntity subtask : task.getSubTasks()) {
            subtasks.add(mapToTaskDto(subtask));
        }
        for (TaskParameter parameter : task.getTaskParameters()) {
            parameters.add(mapToTaskParameterDto(parameter));
        }
        return new TaskDto(task.getId(), task.getStatus(), task.getTaskName(), parameters, subtasks);
    }

    @Override
    public TaskParameter mapToTaskParameter(TaskParameterDto parameter) {
        return new TaskParameter(parameter.getType(), parameter.getTaskName(), parameter.getValue());
    }

    @Override
    public TaskParameterDto mapToTaskParameterDto(TaskParameter parameter) {
        return new TaskParameterDto(parameter.getType(), parameter.getTaskName(), parameter.getValue());
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
