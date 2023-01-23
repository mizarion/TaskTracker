package com.consist.taskboot.component.mapper;

import com.consist.taskboot.model.dto.TaskDto;
import com.consist.taskboot.model.dto.TaskParameterDto;
import com.consist.taskboot.model.entity.TaskEntity;
import com.consist.taskboot.model.entity.TaskParameter;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface TaskMapper extends RowMapper<TaskEntity> {

    TaskEntity mapToTaskEntity(TaskDto taskDto);

    TaskDto mapToTaskDto(TaskEntity taskEntity);

    TaskParameter mapToTaskParameter(TaskParameterDto taskParameterDto);

    TaskParameterDto mapToTaskParameterDto(TaskParameter taskParameter);

    TaskEntity mapRow(@SuppressWarnings("NullableProblems") ResultSet resultSet, int i) throws SQLException;

}
