package com.consist.taskboot.model.entity;

import com.consist.taskboot.model.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TaskEntity {
    private final Integer id;
    private final Status status;
    private final String taskName;
    private final Integer parentId;
    private final List<TaskParameter> taskParameters = new ArrayList<>();
    private final List<TaskEntity> subTasks = new ArrayList<>();

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
        for (TaskParameter param : taskParameters) {
            this.taskParameters.add(new TaskParameter(param.getType(), param.getTaskName(), param.getValue(), id));
        }
        for (TaskEntity subtask : subTasks) {
            this.subTasks.add(new TaskEntity(subtask.getId(), subtask.getStatus(), subtask.getTaskName(), subtask.taskParameters, subtask.getSubTasks(), id));
        }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskEntity that = (TaskEntity) o;
        return id.equals(that.id) && status == that.status && taskName.equals(that.taskName) && Objects.equals(taskParameters, that.taskParameters) && Objects.equals(subTasks, that.subTasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, taskName, taskParameters, subTasks);
    }

    @Override
    public String toString() {
        return "TaskEntity(id=" + this.getId() + ", status=" + this.getStatus() + ", taskName=" + this.getTaskName() + ", parentId=" + this.getParentId() + ", taskParameters=" + this.getTaskParameters() + ", subTasks=" + this.getSubTasks() + ")";
    }

    public Integer getParentId() {
        return parentId;
    }
}