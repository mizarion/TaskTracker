package com.consist.task.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class TaskParameterDto {
    String type;
    String taskName;
    String value;

    public TaskParameterDto(
            @JsonProperty("type") String type,
            @JsonProperty("taskName") String taskName,
            @JsonProperty("value") String value) {
        this.type = type;
        this.taskName = taskName;
        this.value = value;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskParameterDto that = (TaskParameterDto) o;
        return type.equals(that.type) && taskName.equals(that.taskName) && value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, taskName, value);
    }

    @Override
    public String toString() {
        return "TaskParameterDto(type=" + this.getType() + ", taskName=" + this.getTaskName() + ", value=" + this.getValue() + ")";
    }
}
