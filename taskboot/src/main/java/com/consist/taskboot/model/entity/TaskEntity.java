package com.consist.taskboot.model.entity;

import com.consist.taskboot.model.Status;

import java.util.List;


public record TaskEntity(Integer id, Status status, String taskName, List<TaskParameter> taskParameters,
                         List<TaskEntity> subTasks, Integer parentId) {
    public TaskEntity(Integer id, Status status, String taskName) {
        this(id, status, taskName, List.of(), List.of(), null);
    }

    public TaskEntity(Integer id, Status status, String taskName, Integer parentId) {
        this(id, status, taskName, List.of(), List.of(), parentId);
    }

    public TaskEntity(Integer id, Status status, String taskName, List<TaskParameter> taskParameters) {
        this(id, status, taskName, taskParameters, List.of(), null);
    }

    public TaskEntity(Integer id, Status status, String taskName, List<TaskParameter> taskParameters,
                      List<TaskEntity> subTasks) {
        this(id, status, taskName, taskParameters, subTasks, null);
    }

    public TaskEntity(Integer id, Status status, String taskName, List<TaskParameter> taskParameters,
                      List<TaskEntity> subTasks, Integer parentId) {
        this.id = id;
        this.status = status;
        this.taskName = taskName;
        this.parentId = parentId;
        this.taskParameters = taskParameters.stream()
                .map(param -> new TaskParameter(param.type(), param.taskName(), param.value(), id))
                .toList();
        this.subTasks = subTasks.stream()
                .map(sub -> new TaskEntity(sub.id, sub.status, sub.taskName, sub.taskParameters, sub.subTasks, id))
                .toList();
    }

    public Integer getId() {
        return this.id;
    }

    public Status getStatus() {
        return this.status;
    }

    public String getTaskName() {
        return this.taskName;
    }

    public List<TaskParameter> getTaskParameters() {
        return this.taskParameters;
    }

    public List<TaskEntity> getSubTasks() {
        return this.subTasks;
    }

    public Integer getParentId() {
        return parentId;
    }
}