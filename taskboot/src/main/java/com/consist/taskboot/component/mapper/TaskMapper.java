package com.consist.taskboot.component.mapper;

import com.consist.taskboot.model.dto.TaskDto;
import com.consist.taskboot.model.entity.TaskEntity;
import org.springframework.jdbc.core.RowMapper;

public interface TaskMapper extends RowMapper<TaskEntity> {

    TaskEntity mapToTaskEntity(TaskDto taskDto);

    TaskDto mapToTaskDto(TaskEntity taskEntity);

}
