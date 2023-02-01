package com.consist.taskboot.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public record TaskParameterDto(@JsonProperty("param_type") @NotNull() String paramType,
                               @JsonProperty("param_name") @NotNull String paramName,
                               @JsonProperty("param_value") @NotNull String paramValue) {

}
