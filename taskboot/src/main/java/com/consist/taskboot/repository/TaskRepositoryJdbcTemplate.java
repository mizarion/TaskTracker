package com.consist.taskboot.repository;

import com.consist.taskboot.component.ParameterMapper;
import com.consist.taskboot.component.TaskMapper;
import com.consist.taskboot.component.TaskUtils;
import com.consist.taskboot.component.separator.EntitySeparator;
import com.consist.taskboot.component.separator.EntitySeparator.SeparateResult;
import com.consist.taskboot.model.entity.TaskEntity;
import com.consist.taskboot.model.entity.TaskParameter;
import com.consist.taskboot.repository.batch.*;
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
    private final EntitySeparator<TaskParameter> paramSeparator;
    private final EntitySeparator<TaskEntity> taskSeparator;

    public TaskRepositoryJdbcTemplate(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedTemplate,
                                      TaskMapper taskMapper, ParameterMapper paramMapper, TaskUtils taskUtils,
                                      EntitySeparator<TaskParameter> paramSeparator, EntitySeparator<TaskEntity> taskSeparator) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedTemplate = namedTemplate;
        this.taskMapper = taskMapper;
        this.paramMapper = paramMapper;
        this.taskUtils = taskUtils;
        this.paramSeparator = paramSeparator;
        this.taskSeparator = taskSeparator;
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

    @Override
    public void update(TaskEntity taskEntity) {
        List<TaskEntity> dbTasks = taskUtils.taskTree2List(query(new TaskSpecificationById(taskEntity.getId())).get(0));
        List<TaskEntity> newTasks = taskUtils.taskTree2List(taskEntity);
        for (TaskEntity task : newTasks) {
            for (TaskParameter param : task.getTaskParameters()) {
                param.setTaskId(task.getId());
            }
        }
        SeparateResult<TaskEntity> separateTask = taskSeparator.separate(dbTasks, newTasks);
        // delete
        jdbcTemplate.batchUpdate(DeleteTaskByIdBps.SQL, new DeleteTaskByIdBps(separateTask.deleted()));
        // insert task
        jdbcTemplate.batchUpdate(InsertTaskBps.SQL, new InsertTaskBps(separateTask.created()));
        // params for new inserted tasks
        List<TaskParameter> toInsertParams = separateTask.created().stream()
                .map(TaskEntity::getTaskParameters)
                .collect(ArrayList::new, List::addAll, List::addAll);
        // update
        Map.Entry<List<TaskEntity>, List<TaskEntity>> toUpdate = separateTask.updated();
        List<TaskEntity> dbTaskToUpdate = toUpdate.getKey();
        List<TaskEntity> newTaskToUpdate = toUpdate.getValue();
        List<TaskParameter> toDeleteParams = new ArrayList<>();
        // update params
        for (int i = 0; i < toUpdate.getValue().size(); i++) {
            List<TaskParameter> dbParams = dbTaskToUpdate.get(i).getTaskParameters();
            List<TaskParameter> newParams = newTaskToUpdate.get(i).getTaskParameters();
            SeparateResult<TaskParameter> separateParams = paramSeparator.separate(dbParams, newParams);
            toDeleteParams.addAll(separateParams.deleted());  // Параметры из бд, которых нет в новой версии
            toInsertParams.addAll(separateParams.created());  // Новые параметры которых нет в бд
        }
        jdbcTemplate.batchUpdate(DeleteParamBps.SQL, new DeleteParamBps(toDeleteParams));
        jdbcTemplate.batchUpdate(InsertParamBps.SQL, new InsertParamBps(toInsertParams));
        jdbcTemplate.batchUpdate(UpdateTaskBps.SQL, new UpdateTaskBps(newTaskToUpdate));
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
