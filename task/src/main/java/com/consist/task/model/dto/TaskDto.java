package com.consist.task.model.dto;

import com.consist.task.model.Status;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class TaskDto {

    private final Integer id;
    private final Status status;
    private final String taskName;
    private List<TaskParameterDto> taskParameters = new ArrayList<>();
    private List<TaskDto> subTasks = new ArrayList<>();

    public TaskDto(Integer id, Status status, String taskName) {
        this.id = id;
        this.status = status;
        this.taskName = taskName;
    }

    public TaskDto(Integer id,
                   Status status,
                   String taskName,
                   List<TaskParameterDto> taskParameters) {
        this.id = id;
        this.status = status;
        this.taskName = taskName;
        this.taskParameters = taskParameters;
    }

    public TaskDto(@JsonProperty("id") Integer id,
                   @JsonProperty("status") Status status,
                   @JsonProperty("name") String taskName,
                   @JsonProperty("parameters") List<TaskParameterDto> taskParameters,
                   @JsonProperty("subtasks") List<TaskDto> subTasks) {
        this.id = id;
        this.status = status;
        this.taskName = taskName;
        this.taskParameters = taskParameters;
        this.subTasks = subTasks;
    }

    public void createSubTask(TaskDto subtaskDto) {
        if (subtaskDto.equals(this)) {
            throw new IllegalArgumentException("attempt to add a task as a subtask of itself");
        }
        subTasks.add(subtaskDto);
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
