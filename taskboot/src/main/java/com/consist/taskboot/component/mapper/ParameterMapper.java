package com.consist.taskboot.component.mapper;

import com.consist.taskboot.model.entity.TaskParameter;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ParameterMapper extends RowMapper<TaskParameter> {

    TaskParameter mapRow(@SuppressWarnings("NullableProblems") ResultSet resultSet, int i) throws SQLException;

}
