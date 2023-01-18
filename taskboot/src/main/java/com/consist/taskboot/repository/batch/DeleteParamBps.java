package com.consist.taskboot.repository.batch;

import com.consist.taskboot.model.entity.TaskParameter;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class DeleteParamBps implements BatchPreparedStatementSetter {


    public static final String SQL = "DELETE FROM taskparameters WHERE task_id = ? AND type=? AND param_name = ? AND value = ?";

    private final Integer id;
    private final List<TaskParameter> deleteParams;

    public DeleteParamBps(Integer id, List<TaskParameter> deleteParams) {
        this.id = id;
        this.deleteParams = deleteParams;
    }

    @Override
    public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
        preparedStatement.setInt(1, id);
        preparedStatement.setString(2, deleteParams.get(i).getType());
        preparedStatement.setString(3, deleteParams.get(i).getTaskName());
        preparedStatement.setString(4, deleteParams.get(i).getValue());
    }

    @Override
    public int getBatchSize() {
        return deleteParams.size();
    }
}

