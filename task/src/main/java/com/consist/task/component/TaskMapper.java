package com.consist.task.component;

import com.consist.task.model.dto.TaskDto;
import com.consist.task.model.dto.TaskParameterDto;
import com.consist.task.model.entity.TaskEntity;
import com.consist.task.model.entity.TaskParameter;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface TaskMapper extends RowMapper<TaskEntity>  {

    TaskEntity mapToTaskEntity(TaskDto taskDto);

    TaskDto mapToTaskDto(TaskEntity taskEntity);

    TaskParameter mapToTaskParameter(TaskParameterDto taskParameterDto);

    TaskParameterDto mapToTaskParameterDto(TaskParameter taskParameter);

    TaskEntity mapRow(ResultSet resultSet, int i) throws SQLException;

    TaskEntity mapRow(ResultSet resultSet) throws SQLException;

}
