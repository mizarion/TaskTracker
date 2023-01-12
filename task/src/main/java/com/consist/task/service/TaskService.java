package com.consist.task.service;

import com.consist.task.model.dto.TaskDto;
import com.consist.task.model.entity.TaskEntity;

public interface TaskService {

    TaskDto findById(Integer id);

    void create(TaskEntity taskDto);

    void update(TaskEntity taskDto);

    void deleteById(Integer id);
}
