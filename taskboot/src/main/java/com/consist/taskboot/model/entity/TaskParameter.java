package com.consist.taskboot.model.entity;


public record TaskParameter(String type, String taskName, String value, Integer taskId) {
    public TaskParameter(String type, String taskName, String value) {
        this(type, taskName, value, null);
    }
}
