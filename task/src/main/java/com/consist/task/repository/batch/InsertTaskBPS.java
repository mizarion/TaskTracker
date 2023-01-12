package com.consist.task.repository.batch;

import com.consist.task.model.entity.TaskEntity;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

public class InsertTaskBPS implements BatchPreparedStatementSetter {

    public static final String SQL = "INSERT INTO consisttask (task_id, status, name, parent_id) VALUES (?,?,?,?)";

    private final List<TaskEntity> allTasks;

    public InsertTaskBPS(List<TaskEntity> allTasks) {
        this.allTasks = allTasks;
    }

    @Override
    public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
        preparedStatement.setInt(1, allTasks.get(i).getId());
        preparedStatement.setString(2, allTasks.get(i).getStatus().name());
        preparedStatement.setString(3, allTasks.get(i).getTaskName());
        if (allTasks.get(i).getParentId() == null) {
            preparedStatement.setNull(4, Types.INTEGER);
        } else {
            preparedStatement.setInt(4, allTasks.get(i).getParentId());
        }
    }

    @Override
    public int getBatchSize() {
        return allTasks.size();
    }
}
