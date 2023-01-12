package com.consist.task.repository.batch;

import com.consist.task.model.entity.TaskParameter;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class InsertParamBPS implements BatchPreparedStatementSetter {

    public static final String SQL = "INSERT INTO taskparameters (type, param_name, value, task_id) VALUES (?,?,?,?)";

    private final List<TaskParameter> allParams;

    private Integer id = null;

    public InsertParamBPS(List<TaskParameter> allParams) {
        this.allParams = allParams;
    }

    public InsertParamBPS(List<TaskParameter> allParams, Integer id) {
        this.allParams = allParams;
        this.id = id;
    }

    @Override
    public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
        preparedStatement.setString(1, allParams.get(i).getType());
        preparedStatement.setString(2, allParams.get(i).getTaskName());
        preparedStatement.setString(3, allParams.get(i).getValue());
        if (id == null) {
            preparedStatement.setInt(4, allParams.get(i).getTaskId());
        } else {
            preparedStatement.setInt(4, id);
        }
    }

    @Override
    public int getBatchSize() {
        return allParams.size();
    }
}
