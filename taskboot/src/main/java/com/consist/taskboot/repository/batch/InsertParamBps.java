package com.consist.taskboot.repository.batch;

import com.consist.taskboot.model.entity.TaskParameter;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class InsertParamBps implements BatchPreparedStatementSetter {

    public static final String SQL = "INSERT INTO taskparameters (type, param_name, value, task_id) VALUES (?,?,?,?)";

    private final List<TaskParameter> allParams;

    public InsertParamBps(List<TaskParameter> allParams) {
        this.allParams = allParams;
    }

    @Override
    public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
        preparedStatement.setString(1, allParams.get(i).getType());
        preparedStatement.setString(2, allParams.get(i).getTaskName());
        preparedStatement.setString(3, allParams.get(i).getValue());
        preparedStatement.setInt(4, allParams.get(i).getTaskId());
    }

    @Override
    public int getBatchSize() {
        return allParams.size();
    }
}
