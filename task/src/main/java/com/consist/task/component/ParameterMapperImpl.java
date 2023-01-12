package com.consist.task.component;

import com.consist.task.model.entity.TaskParameter;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ParameterMapperImpl implements ParameterMapper {

    @Override
    public TaskParameter mapRow(ResultSet resultSet, int i) throws SQLException {
        return new TaskParameter(
                resultSet.getString("type"),
                resultSet.getString("param_name"),
                resultSet.getString("value"),
                resultSet.getInt("task_id"));
    }

    @Override
    public TaskParameter mapRow(ResultSet resultSet) throws SQLException {
        return new TaskParameter(
                resultSet.getString("type"),
                resultSet.getString("param_name"),
                resultSet.getString("value"),
                resultSet.getInt("task_id"));
    }
}
