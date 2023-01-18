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
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
@Primary
public class TaskRepositoryJdbcTemplate implements TaskRepository {

    private final JdbcTemplate jdbcTemplate;
    private final TaskMapper taskMapper;
    private final ParameterMapper paramMapper;

    public TaskRepositoryJdbcTemplate(JdbcTemplate jdbcTemplate, TaskMapper taskMapper, ParameterMapper paramMapper, Schema schemaSql) {
        this.jdbcTemplate = jdbcTemplate;
        this.taskMapper = taskMapper;
        this.paramMapper = paramMapper;
        jdbcTemplate.execute(schemaSql.toString());
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
            Integer id = ((TaskSpecificationById) taskSpecification).getId();
            List<TaskEntity> allTasks = jdbcTemplate.query(SELECT_TASK_RECURSIVE, new Object[]{id}, taskMapper);
            // select params
            List<Integer> taskIds = new ArrayList<>();
            StringBuilder idsBuilder = new StringBuilder();
            String separator = "";
            for (TaskEntity task : allTasks) {
                taskIds.add(task.getId());
                idsBuilder.append(separator).append("?");
                separator = ",";
            }
            List<TaskParameter> listParam = jdbcTemplate.query(
                    String.format("SELECT * FROM taskparameters WHERE task_id IN (%s)", idsBuilder),
                    taskIds.toArray(), paramMapper);

            TaskEntity rootTask = TaskUtils.taskList2Tree(allTasks, listParam, allTasks.get(0));
            return Collections.singletonList(rootTask);

        } else {
            List<TaskEntity> taskEntities = new ArrayList<>();
            List<TaskEntity> allTasks = jdbcTemplate.query("SELECT * FROM consisttask", taskMapper);
            List<TaskParameter> allParams = jdbcTemplate.query("SELECT * FROM taskparameters", paramMapper);
            for (TaskEntity i : allTasks) {
                if (taskSpecification.specified(i)) {
                    taskEntities.add(TaskUtils.taskList2Tree(allTasks, allParams, i));
                }
            }
            return taskEntities;
        }
    }

    @Override
    public void create(TaskEntity taskEntity) {
        final List<TaskEntity> allTasks = TaskUtils.taskTree2List(taskEntity);
        final List<TaskParameter> allParams = TaskUtils.paramTree2List(taskEntity);

        jdbcTemplate.batchUpdate(InsertTaskBPS.SQL, new InsertTaskBPS(allTasks));

        jdbcTemplate.batchUpdate(InsertParamBPS.SQL, new InsertParamBPS(allParams));
    }

    private void updateParams(final Integer id, final List<TaskParameter> dbParams, final List<TaskParameter> newParams) {

        if (newParams.isEmpty()) {
            jdbcTemplate.update("DELETE FROM taskparameters WHERE task_id = ?", id);
            return;
        }

        if (dbParams.isEmpty()) {
            jdbcTemplate.batchUpdate(InsertParamBPS.SQL, new InsertParamBPS(newParams, id));
            return;
        }

        // удаляем параметры, которых нет в новой версии
        final List<TaskParameter> deleteParams = new ArrayList<>();
        for (TaskParameter dbParam : dbParams) {
            if (!newParams.contains(dbParam)) {
                deleteParams.add(dbParam);
            }
        }
        jdbcTemplate.batchUpdate(DeleteParamBPS.SQL, new DeleteParamBPS(id, deleteParams));

        // Вставляем все параметры которых нет в бд
        final List<TaskParameter> insertParams = new ArrayList<>();
        for (TaskParameter newParam : newParams) {
            if (!dbParams.contains(newParam)) {
                insertParams.add(newParam);
            }
        }
        jdbcTemplate.batchUpdate(InsertParamBPS.SQL, new InsertParamBPS(insertParams, id));
    }

    @Override
    public void update(TaskEntity taskEntity) {
        List<TaskEntity> dbTasks = TaskUtils.taskTree2List(query(new TaskSpecificationById(taskEntity.getId())).get(0));
        List<TaskEntity> newTasks = TaskUtils.taskTree2List(taskEntity);
        for (TaskEntity task : newTasks) {
            for (TaskParameter param : task.getTaskParameters()) {
                param.setTaskId(task.getId());
            }
        }
        // delete
        List<TaskEntity> toDelete = TaskUtils.getNonIntersectionById(dbTasks, newTasks);
        jdbcTemplate.batchUpdate(DeleteTaskByIdBPS.SQL, new DeleteTaskByIdBPS(toDelete));

        // create
        List<TaskEntity> toInsert = TaskUtils.getNonIntersectionById(newTasks, dbTasks);
        List<TaskParameter> toInsertParams = new ArrayList<>();
        for (TaskEntity task : toInsert) {
            toInsertParams.addAll(task.getTaskParameters());
        }

        jdbcTemplate.batchUpdate(InsertTaskBPS.SQL, new InsertTaskBPS(toInsert));
        jdbcTemplate.batchUpdate(InsertParamBPS.SQL, new InsertParamBPS(toInsertParams));
        // update
        for (TaskEntity newSubtask : newTasks) {
            for (TaskEntity dbSubtask : dbTasks)
                if (newSubtask.getId().equals(dbSubtask.getId())) {
                    if (!dbSubtask.getTaskParameters().equals(newSubtask.getTaskParameters())) {
                        updateParams(newSubtask.getId(), dbSubtask.getTaskParameters(), newSubtask.getTaskParameters());
                    }
                    if (!dbSubtask.getStatus().equals(newSubtask.getStatus())
                        || !dbSubtask.getTaskName().equals(newSubtask.getTaskName())) {
                        jdbcTemplate.update("UPDATE consisttask SET status=?, name=? WHERE task_id=?",
                                newSubtask.getStatus().name(), newSubtask.getTaskName(), newSubtask.getId());
                    }
                }
        }
    }

    @Override
    public void deleteById(Integer id) {
        final List<TaskEntity> tasks = jdbcTemplate.query(SELECT_TASK_RECURSIVE, new Object[]{id}, taskMapper);
        int[] ints = jdbcTemplate.batchUpdate(DeleteTaskByIdBPS.SQL, new DeleteTaskByIdBPS(tasks));
        int sum = 0;
        for (int i : ints) {
            sum += i;
        }
        if (sum == 0) {
            throw new IllegalArgumentException("there are no task with id=" + id);
        }
    }
}
