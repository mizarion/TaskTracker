package com.consist.taskboot.model.entity;


import java.util.Objects;

public class TaskParameter {
    private final String type;
    private final String taskName;
    private final String value;
    private Integer taskId;

    public TaskParameter(String type, String taskName, String value) {
        this.type = type;
        this.taskName = taskName;
        this.value = value;
    }

    public TaskParameter(String type, String taskName, String value, Integer taskId) {
        this.type = type;
        this.taskName = taskName;
        this.value = value;
        this.taskId = taskId;
    }

    public String getType() {
        return this.type;
    }

    public String getTaskName() {
        return this.taskName;
    }

    public String getValue() {
        return this.value;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskParameter that = (TaskParameter) o;
        return type.equals(that.type) && taskName.equals(that.taskName) && value.equals(that.value) && Objects.equals(taskId, that.taskId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, taskName, value, taskId);
    }

    @Override
    public String toString() {
        return "TaskParameter{" +
               "type='" + type + '\'' +
               ", taskName='" + taskName + '\'' +
               ", value='" + value + '\'' +
               ", task_id=" + taskId +
               '}';
    }
}
