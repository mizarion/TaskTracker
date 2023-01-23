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
        TaskEntity task = tasks.get(i);
        ps.setString(1, task.getStatus().name());
        ps.setString(2, task.getTaskName());
        ps.setInt(3, task.getId());
    }

    @Override
    public int getBatchSize() {
        return tasks.size();
    }
}