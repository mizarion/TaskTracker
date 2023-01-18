package com.consist.taskboot.component;

import com.consist.taskboot.model.entity.TaskParameter;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ParameterMapper extends RowMapper<TaskParameter> {

    TaskParameter mapRow(ResultSet resultSet, int i) throws SQLException;

    TaskParameter mapRow(ResultSet resultSet) throws SQLException;

}
