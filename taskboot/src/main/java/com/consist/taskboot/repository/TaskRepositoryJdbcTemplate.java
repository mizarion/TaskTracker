package com.consist.taskboot.repository;

import com.consist.taskboot.component.ParameterMapper;
import com.consist.taskboot.component.TaskMapper;
import com.consist.taskboot.component.TaskUtils;
import com.consist.taskboot.model.entity.TaskEntity;
import com.consist.taskboot.model.entity.TaskParameter;
import com.consist.taskboot.repository.batch.DeleteParamBps;
import com.consist.taskboot.repository.batch.DeleteTaskByIdBps;
import com.consist.taskboot.repository.batch.InsertParamBps;
import com.consist.taskboot.repository.batch.InsertTaskBps;
import com.consist.taskboot.repository.specification.TaskSpecification;
import com.consist.taskboot.repository.specification.TaskSpecificationById;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Repository
@Primary
public class TaskRepositoryJdbcTemplate implements TaskRepository {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedTemplate;
    private final TaskMapper taskMapper;
    private final ParameterMapper paramMapper;

    private final TaskUtils taskUtils;


    public TaskRepositoryJdbcTemplate(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedTemplate,
                                      TaskMapper taskMapper, ParameterMapper paramMapper, TaskUtils taskUtils) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedTemplate = namedTemplate;
        this.taskMapper = taskMapper;
        this.paramMapper = paramMapper;
        this.taskUtils = taskUtils;
    }

    private static final String SELECT_TASK_RECURSIVE_NAMED = """
            WITH RECURSIVE gettask(task_id, name, status, parent_id) AS (
                SELECT t1.task_id, t1.name, t1.status, t1.parent_id
                    FROM consisttask t1 WHERE t1.task_id = :id
                UNION
                SELECT t2.task_id, t2.name, t2.status, t2.parent_id
                    FROM consisttask t2 join gettask on (gettask.task_id = t2.parent_id)
            )
            SELECT * FROM gettask""";

    @Override
    public List<TaskEntity> query(TaskSpecification taskSpecification) {
        if (taskSpecification instanceof TaskSpecificationById taskSpecificationById) {
            // select tasks
            Integer id = taskSpecificationById.getId();
            List<TaskEntity> tasks = namedTemplate.query(SELECT_TASK_RECURSIVE_NAMED, Map.of("id", id), taskMapper);
            // select params
            List<Integer> taskIds = tasks.stream().map(TaskEntity::getId).toList();
            List<TaskParameter> listParam = namedTemplate.query("SELECT * FROM taskparameters WHERE task_id IN (:ids)",
                    Map.of("ids", taskIds), paramMapper);

            TaskEntity rootTask = taskUtils.taskList2Tree(tasks, listParam, tasks.get(0));
            return Collections.singletonList(rootTask);

        } else {
            List<TaskEntity> taskEntities = new ArrayList<>();
            List<TaskEntity> allTasks = jdbcTemplate.query("SELECT * FROM consisttask", taskMapper);
            List<TaskParameter> allParams = jdbcTemplate.query("SELECT * FROM taskparameters", paramMapper);
            for (TaskEntity i : allTasks) {
                if (taskSpecification.specified(i)) {
                    taskEntities.add(taskUtils.taskList2Tree(allTasks, allParams, i));
                }
            }
            return taskEntities;
        }
    }

    @Override
    public void create(TaskEntity taskEntity) {
        final List<TaskEntity> allTasks = taskUtils.taskTree2List(taskEntity);
        final List<TaskParameter> allParams = taskUtils.paramTree2List(taskEntity);

        jdbcTemplate.batchUpdate(InsertTaskBps.SQL, new InsertTaskBps(allTasks));
        jdbcTemplate.batchUpdate(InsertParamBps.SQL, new InsertParamBps(allParams));
    }

    private void updateParams(Integer id, List<TaskParameter> dbParams, List<TaskParameter> newParams) {

        if (newParams.isEmpty()) {
            jdbcTemplate.update("DELETE FROM taskparameters WHERE task_id = ?", id);
            return;
        }

        if (dbParams.isEmpty()) {
            jdbcTemplate.batchUpdate(InsertParamBps.SQL, new InsertParamBps(newParams, id));
            return;
        }

        // удаляем параметры, которых нет в новой версии
        final List<TaskParameter> deleteParams = new ArrayList<>();
        for (TaskParameter dbParam : dbParams) {
            if (!newParams.contains(dbParam)) {
                deleteParams.add(dbParam);
            }
        }
        jdbcTemplate.batchUpdate(DeleteParamBps.SQL, new DeleteParamBps(id, deleteParams));

        // Вставляем все параметры которых нет в бд
        final List<TaskParameter> insertParams = new ArrayList<>();
        for (TaskParameter newParam : newParams) {
            if (!dbParams.contains(newParam)) {
                insertParams.add(newParam);
            }
        }
        jdbcTemplate.batchUpdate(InsertParamBps.SQL, new InsertParamBps(insertParams, id));
    }

    @Override
    public void update(TaskEntity taskEntity) {
        List<TaskEntity> dbTasks = taskUtils.taskTree2List(query(new TaskSpecificationById(taskEntity.getId())).get(0));
        List<TaskEntity> newTasks = taskUtils.taskTree2List(taskEntity);
        for (TaskEntity task : newTasks) {
            for (TaskParameter param : task.getTaskParameters()) {
                param.setTaskId(task.getId());
            }
        }
        // delete
        List<TaskEntity> toDelete = taskUtils.getNonIntersectionById(dbTasks, newTasks);
        jdbcTemplate.batchUpdate(DeleteTaskByIdBps.SQL, new DeleteTaskByIdBps(toDelete));

        // create
        List<TaskEntity> toInsert = taskUtils.getNonIntersectionById(newTasks, dbTasks);
        List<TaskParameter> toInsertParams = new ArrayList<>();
        for (TaskEntity task : toInsert) {
            toInsertParams.addAll(task.getTaskParameters());
        }

        jdbcTemplate.batchUpdate(InsertTaskBps.SQL, new InsertTaskBps(toInsert));
        jdbcTemplate.batchUpdate(InsertParamBps.SQL, new InsertParamBps(toInsertParams));
        // update
        for (TaskEntity newSubtask : newTasks) {
            for (TaskEntity dbSubtask : dbTasks)
                if (newSubtask.getId().equals(dbSubtask.getId())) {
                    // update params
                    if (!dbSubtask.getTaskParameters().equals(newSubtask.getTaskParameters())) {
                        updateParams(newSubtask.getId(), dbSubtask.getTaskParameters(), newSubtask.getTaskParameters());
                    }
                    // update task property
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
        List<TaskEntity> tasks = namedTemplate.query(SELECT_TASK_RECURSIVE_NAMED, Map.of("id", id), taskMapper);
        int[] ints = jdbcTemplate.batchUpdate(DeleteTaskByIdBps.SQL, new DeleteTaskByIdBps(tasks));
        int sum = 0;
        for (int i : ints) {
            sum += i;
        }
        if (sum == 0) {
            throw new IllegalArgumentException("there are no task with id=" + id);
        }
    }
}
