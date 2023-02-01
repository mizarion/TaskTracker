package com.consist.taskboot.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;


public record TaskParameterDto(String type, String taskName, String value) {
    public String getType() {
        return this.type;
    }

    public String getTaskName() {
        return this.taskName;
    }

    public String getValue() {
        return this.value;
    }
}
