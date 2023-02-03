package com.consist.taskboot.repository.batch;

import com.consist.taskboot.model.entity.TaskEntity;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class UpdateTaskBps implements BatchPreparedStatementSetter {

    public static final String SQL = "UPDATE consisttask SET status=?, name=? WHERE task_id=?";

    private final List<TaskEntity> tasks;

    public UpdateTaskBps(List<TaskEntity> tasks) {
        this.tasks = tasks;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        ps.setObject(1, tasks.get(i).getStatus().name(), java.sql.Types.OTHER);
        ps.setString(2, tasks.get(i).getTaskName());
        ps.setInt(3, tasks.get(i).getId());
    }

    @Override
    public int getBatchSize() {
        return tasks.size();
    }
}