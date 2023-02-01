package com.consist.taskboot.testcontainers.repository;

import com.consist.taskboot.model.Status;
import com.consist.taskboot.model.entity.TaskEntity;
import com.consist.taskboot.model.entity.TaskParameter;
import com.consist.taskboot.repository.TaskRepository;
import com.consist.taskboot.repository.specification.TaskSpecificationById;
import com.consist.taskboot.testcontainers.config.PostgresBaseIT;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TaskRepositoryCreateIT extends PostgresBaseIT {

    @Autowired
    private TaskRepository taskRepository;
    private TaskEntity task;
    private static final Integer FREE_ID = 999;
    private static final List<TaskParameter> params = new ArrayList<>();

    @BeforeEach
    void init() {
        task = new TaskEntity(FREE_ID, Status.READY, "test");
    }

    @AfterEach
    void delete() {
        taskRepository.deleteById(task.getId());
    }

    @Test
    @Order(1)
    void createTask() {
        taskRepository.create(task);
        List<TaskEntity> receivedTask = taskRepository.query(new TaskSpecificationById(task.getId()));
        Assertions.assertEquals(1, receivedTask.size());
        Assertions.assertEquals(task, receivedTask.get(0));
    }

    @Test
    @Order(2)
    void createSubtask() {
        List<TaskEntity> subtasks = new ArrayList<>();
        TaskEntity subtask = new TaskEntity(task.getId() + 1, Status.READY, "", params);
        subtasks.add(subtask);

        TaskEntity newTask = new TaskEntity(task.getId(), task.getStatus(), task.getTaskName(), params, subtasks);

        // create task with 1 subtask
        taskRepository.create(newTask);
        List<TaskEntity> receivedTasks = taskRepository.query(new TaskSpecificationById(task.getId()));
        Assertions.assertEquals(newTask.subTasks().get(0), receivedTasks.get(0).getSubTasks().get(0));
        Assertions.assertEquals(newTask.getSubTasks(), receivedTasks.get(0).getSubTasks());
        Assertions.assertEquals(newTask, receivedTasks.get(0));
    }

    @Test
    void createSubsubtask() {

        List<TaskEntity> subsubtasks = new ArrayList<>();
        TaskEntity subsubtask = new TaskEntity(task.getId() + 2, Status.READY, "", params);
        subsubtasks.add(subsubtask);

        List<TaskEntity> subtasks = new ArrayList<>();
        TaskEntity subtask = new TaskEntity(task.getId() + 1, Status.READY, "", params, subsubtasks);
        subtasks.add(subtask);


        TaskEntity newTask = new TaskEntity(task.getId(), task.getStatus(), task.getTaskName(), params, subtasks);

        // create task with 1 subtask
        taskRepository.create(newTask);
        List<TaskEntity> receivedTasks = taskRepository.query(new TaskSpecificationById(task.getId()));

        Assertions.assertEquals(subtask.subTasks().get(0), receivedTasks.get(0).subTasks().get(0).subTasks().get(0));
        Assertions.assertEquals(subtask.subTasks(), receivedTasks.get(0).subTasks().get(0).subTasks());

        Assertions.assertEquals(newTask.subTasks().get(0), receivedTasks.get(0).subTasks().get(0));
        Assertions.assertEquals(newTask.subTasks(), receivedTasks.get(0).subTasks());
        Assertions.assertEquals(newTask, receivedTasks.get(0));
    }

    @Test
    void createParam() {
        List<TaskParameter> params = new ArrayList<>();
        params.add(new TaskParameter("1", "1", "1"));
        params.add(new TaskParameter("2", "2", "2"));

        TaskEntity newTask = new TaskEntity(task.getId(), task.getStatus(), task.getTaskName(), params);

        // create task with 1 subtask
        taskRepository.create(newTask);
        List<TaskEntity> receivedTasks = taskRepository.query(new TaskSpecificationById(task.getId()));

        Assertions.assertEquals(newTask.getTaskParameters(), receivedTasks.get(0).getTaskParameters());
        Assertions.assertEquals(newTask, receivedTasks.get(0));
    }

    @Test
    void createSubtaskParam() {
        List<TaskParameter> subparams = Arrays.asList(new TaskParameter("11", "11", "11"),
                new TaskParameter("22", "22", "22"));
        List<TaskEntity> subtasks = Collections.singletonList(new TaskEntity(task.getId() + 1, Status.READY, "", subparams));
        List<TaskParameter> params = Arrays.asList(new TaskParameter("1", "1", "1"),
                new TaskParameter("2", "2", "2"));

        TaskEntity newTask = new TaskEntity(task.getId(), task.getStatus(), task.getTaskName(), params, subtasks);

        taskRepository.create(newTask);
        List<TaskEntity> receivedTasks = taskRepository.query(new TaskSpecificationById(task.getId()));

        Assertions.assertEquals(subtasks.get(0).getTaskParameters(), receivedTasks.get(0).getSubTasks().get(0).getTaskParameters());
        Assertions.assertEquals(newTask.getTaskParameters(), receivedTasks.get(0).getTaskParameters());
        Assertions.assertEquals(newTask, receivedTasks.get(0));
    }
}
