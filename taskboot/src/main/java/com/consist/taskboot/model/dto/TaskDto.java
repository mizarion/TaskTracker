package com.consist.taskboot.model.dto;

import com.consist.taskboot.model.Status;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;


public class TaskDto {

    private final Integer id;
    private final Status status;
    private final String taskName;
    private final List<TaskParameterDto> taskParameters;
    private final List<TaskDto> subTasks;

    public TaskDto(Integer id, Status status, String taskName) {
        this(id, status, taskName, List.of(), List.of());

    }

    public TaskDto(Integer id, Status status, String taskName, List<TaskParameterDto> taskParameters) {
        this(id, status, taskName, taskParameters, List.of());
    }

    public TaskDto(@JsonProperty("id") Integer id,
                   @JsonProperty("status") Status status,
                   @JsonProperty("name") String name,
                   @JsonProperty("parameters") List<TaskParameterDto> parameters,
                   @JsonProperty("subtasks") List<TaskDto> subtasks) {
        this.id = id;
        this.status = status;
        this.taskName = name;
        this.taskParameters = parameters;
        this.subTasks = subtasks;
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

    public List<TaskDto> getSubTasks() {
        return this.subTasks;
    }

    public List<TaskParameterDto> getTaskParameters() {
        return this.taskParameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskDto that = (TaskDto) o;
        return id.equals(that.id) && status == that.status && taskName.equals(that.taskName) && Objects.equals(taskParameters, that.taskParameters) && Objects.equals(subTasks, that.subTasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, taskName, taskParameters, subTasks);
    }

    @Override
    public String toString() {
        return "TaskDto(id=" + this.getId() + ", status=" + this.getStatus() + ", taskName=" + this.getTaskName() + ", subTasks=" + this.getSubTasks() + ", taskParameters=" + this.getTaskParameters() + ")";
    }


}
