package com.consist.task.repository.batch;

import com.consist.task.model.entity.TaskEntity;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class DeleteTaskByIdBPS extends AbstractBPS {

    public static final String SQL = "DELETE FROM consisttask WHERE task_id = ?";
    private final List<TaskEntity> tasks;


    public DeleteTaskByIdBPS(List<TaskEntity> tasks) {

        this.tasks = tasks;
    }

    @Override
    public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
        preparedStatement.setInt(1, tasks.get(i).getId());
    }

    @Override
    public int getBatchSize() {
        return tasks.size();
    }

    @Override
    public String getSQL() {
        return SQL;
    }
}
