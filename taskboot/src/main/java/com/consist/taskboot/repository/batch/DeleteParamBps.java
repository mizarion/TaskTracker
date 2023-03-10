package com.consist.taskboot.repository.batch;

import com.consist.taskboot.model.entity.TaskParameter;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class DeleteParamBps implements BatchPreparedStatementSetter {

    public static final String SQL = "DELETE FROM taskparameters WHERE task_id = ? AND type=? AND param_name = ? AND value = ?";

    private final List<TaskParameter> deleteParams;

    public DeleteParamBps(List<TaskParameter> deleteParams) {
        this.deleteParams = deleteParams;
    }

    @Override
    public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
        preparedStatement.setInt(1, deleteParams.get(i).taskId());
        preparedStatement.setString(2, deleteParams.get(i).type());
        preparedStatement.setString(3, deleteParams.get(i).taskName());
        preparedStatement.setString(4, deleteParams.get(i).value());
    }

    @Override
    public int getBatchSize() {
        return deleteParams.size();
    }
}

