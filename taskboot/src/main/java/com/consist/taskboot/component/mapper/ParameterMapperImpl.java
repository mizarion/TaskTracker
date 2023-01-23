package com.consist.taskboot.component.mapper;

import com.consist.taskboot.model.entity.TaskParameter;
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
}
