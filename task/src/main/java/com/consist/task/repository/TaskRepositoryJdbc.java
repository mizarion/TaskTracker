package com.consist.task.repository;

import com.consist.task.component.ParameterMapper;
import com.consist.task.component.TaskMapper;
import com.consist.task.config.Schema;
import com.consist.task.model.TaskUtils;
import com.consist.task.model.entity.TaskEntity;
import com.consist.task.model.entity.TaskParameter;
import com.consist.task.repository.batch.DeleteParamBPS;
import com.consist.task.repository.batch.DeleteTaskByIdBPS;
import com.consist.task.repository.batch.InsertParamBPS;
import com.consist.task.repository.batch.InsertTaskBPS;
import com.consist.task.repository.specification.TaskSpecification;
import com.consist.task.repository.specification.TaskSpecificationById;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class TaskRepositoryJdbc implements TaskRepository {

    private final DataSource dataSource;
    private final TaskMapper taskMapper;
    private final ParameterMapper paramMapper;

    public TaskRepositoryJdbc(DataSource dataSource, TaskMapper taskMapper, ParameterMapper paramMapper, Schema schemaSql) {
        this.dataSource = dataSource;
        this.taskMapper = taskMapper;
        this.paramMapper = paramMapper;

        try (Connection connection = dataSource.getConnection();
             Statement st = connection.createStatement()) {
            st.execute(schemaSql.toString());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String SELECT_TASK_RECURSIVE = "WITH RECURSIVE gettask(task_id, name, status, parent_id) AS (\n" +
            "    SELECT t1.task_id, t1.name, t1.status, t1.parent_id " +
            "    FROM consisttask t1 WHERE t1.task_id = ?" +
            "  UNION \n" +
            "    SELECT t2.task_id, t2.name, t2.status, t2.parent_id \n" +
            "    FROM consisttask t2 join gettask on (gettask.task_id = t2.parent_id) " +
            "  )\n" +
            "SELECT * FROM gettask";

    @Override
    public List<TaskEntity> query(TaskSpecification taskSpecification) {
        if (taskSpecification instanceof TaskSpecificationById) {
            // select tasks
            List<TaskEntity> allTasks = new ArrayList<>();
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement taskPs = connection.prepareStatement(SELECT_TASK_RECURSIVE)) {
                // select tasks
                taskPs.setInt(1, ((TaskSpecificationById) taskSpecification).getId());
                ResultSet row = taskPs.executeQuery();
                while (row.next()) {
                    allTasks.add(taskMapper.mapRow(row));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            // get ids & prepare statement
            List<Integer> taskIds = new ArrayList<>();
            StringBuilder idsBuilder = new StringBuilder();
            String separator = "";
            for (TaskEntity task : allTasks) {
                taskIds.add(task.getId());
                idsBuilder.append(separator).append("?");
                separator = ",";
            }
            String paramSQl = String.format("SELECT * FROM taskparameters WHERE task_id IN (%s)", idsBuilder);
            // select param
            List<TaskParameter> listParam = new ArrayList<>();
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement paramSt = connection.prepareStatement(paramSQl)) {
                for (int i = 0; i < taskIds.size(); i++) {
                    paramSt.setInt(i + 1, taskIds.get(i));
                }
                ResultSet paramRow = paramSt.executeQuery();
                while (paramRow.next()) {
                    listParam.add(paramMapper.mapRow(paramRow));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            TaskEntity rootTask = TaskUtils.taskList2Tree(allTasks, listParam, allTasks.get(0));
            return Collections.singletonList(rootTask);
        } else {
            return null;
        }
    }

    @Override
    public void create(TaskEntity taskEntity) {
        final List<TaskEntity> allTasks = TaskUtils.taskTree2List(taskEntity);
        final List<TaskParameter> allParams = TaskUtils.paramTree2List(taskEntity);

        try (Connection connection = dataSource.getConnection();
             PreparedStatement insertTPs = connection.prepareStatement(InsertTaskBPS.SQL)) {
            InsertTaskBPS insertTaskBPS = new InsertTaskBPS(allTasks);
            for (int i = 0; i < allTasks.size(); i++) {
                insertTaskBPS.setValues(insertTPs, i);
                insertTPs.addBatch();
            }
            insertTPs.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try (Connection connection = dataSource.getConnection();
             PreparedStatement insertPPs = connection.prepareStatement(InsertParamBPS.SQL)) {
            InsertParamBPS insertParamBPS = new InsertParamBPS(allParams);
            for (int i = 0; i < allParams.size(); i++) {
                insertParamBPS.setValues(insertPPs, i);
                insertPPs.addBatch();
            }
            insertPPs.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateParams(final Integer id, final List<TaskParameter> dbParams, final List<TaskParameter> newParams) {

        if (newParams.isEmpty()) {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement ps = connection.prepareStatement("DELETE FROM taskparameters WHERE task_id = ?")) {
                ps.setInt(1, id);
                ps.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        if (dbParams.isEmpty()) {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement ps = connection.prepareStatement(InsertParamBPS.SQL)) {
                InsertParamBPS insertParamBPS = new InsertParamBPS(newParams, id);
                for (int i = 0; i < newParams.size(); i++) {
                    insertParamBPS.setValues(ps, i);
                    ps.addBatch();
                }
                ps.executeBatch();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        // удаляем параметры, которых нет в новой версии
        final List<TaskParameter> deleteParams = new ArrayList<>();
        for (TaskParameter dbParam : dbParams) {
            if (!newParams.contains(dbParam)) {
                deleteParams.add(dbParam);
            }
        }
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(DeleteParamBPS.SQL)) {
            DeleteParamBPS deleteParamBPS = new DeleteParamBPS(id, deleteParams);
            for (int i = 0; i < newParams.size(); i++) {
                deleteParamBPS.setValues(ps, i);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Вставляем все параметры которых нет в бд
        final List<TaskParameter> insertParams = new ArrayList<>();
        for (TaskParameter newParam : newParams) {
            if (!dbParams.contains(newParam)) {
                insertParams.add(newParam);
            }
        }
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(InsertParamBPS.SQL)) {
            InsertParamBPS insertParamBPS = new InsertParamBPS(insertParams, id);
            for (int i = 0; i < newParams.size(); i++) {
                insertParamBPS.setValues(ps, i);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void update(TaskEntity source, TaskEntity updated) {
        if (!source.getStatus().equals(updated.getStatus()) || !source.getTaskName().equals(updated.getTaskName())) {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement ps = connection.prepareStatement("UPDATE consisttask SET status=?, name=? WHERE task_id=?")) {
                ps.setString(1, updated.getStatus().name());
                ps.setString(2, updated.getTaskName());
                ps.setInt(3, updated.getId());
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        updateParams(updated.getId(), source.getTaskParameters(), updated.getTaskParameters());

        List<TaskEntity> toDelete = new ArrayList<>();
        for (TaskEntity d : TaskUtils.getIntersectionById(source.getSubTasks(), updated.getSubTasks()).getLeft()) {
            toDelete.addAll(TaskUtils.taskTree2List(d));
        }
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(DeleteTaskByIdBPS.SQL)) {
            DeleteTaskByIdBPS deleteTaskByIdBPS = new DeleteTaskByIdBPS(toDelete);
            for (int i = 0; i < toDelete.size(); i++) {
                deleteTaskByIdBPS.setValues(ps, i);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        Pair<List<TaskEntity>, List<Pair<TaskEntity, TaskEntity>>> intersectionById = TaskUtils.getIntersectionById(updated.getSubTasks(), source.getSubTasks());

        for (TaskEntity c : intersectionById.getLeft()) {
            c.setParent(updated.getId());
            create(c);
        }
        for (Pair<TaskEntity, TaskEntity> c : intersectionById.getRight()) {
            update(c.getRight(), c.getLeft());
        }
    }

    @Override
    public void update(TaskEntity taskEntity) {
        TaskEntity source = this.query(new TaskSpecificationById(taskEntity.getId())).get(0);
        update(source, taskEntity);
    }

    @Override
    public void deleteById(Integer id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement taskPs = connection.prepareStatement(SELECT_TASK_RECURSIVE);
             PreparedStatement deletePs = connection.prepareStatement(DeleteTaskByIdBPS.SQL)) {
            // select tasks
            taskPs.setInt(1, id);
            ResultSet row = taskPs.executeQuery();
            List<TaskEntity> tasks = new ArrayList<>();
            while (row.next()) {
                tasks.add(taskMapper.mapRow(row));
            }
            DeleteTaskByIdBPS deleteBPS = new DeleteTaskByIdBPS(tasks);
            for (int i = 0; i < tasks.size(); i++) {
                deleteBPS.setValues(deletePs, i);
                deletePs.addBatch();
            }
            int[] ints = deletePs.executeBatch();
            int sum = 0;
            for (int i : ints) {
                sum += i;
            }
            if (sum == 0) {
                throw new IllegalArgumentException("there are no task with id=" + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
