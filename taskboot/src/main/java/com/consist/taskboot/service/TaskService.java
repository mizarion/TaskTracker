package com.consist.taskboot.service;

import com.consist.taskboot.model.dto.TaskDto;
import com.consist.taskboot.model.entity.TaskEntity;

public interface TaskService {

    TaskDto findById(Integer id);

    void create(TaskEntity taskDto);

    void update(TaskEntity taskDto);

    void deleteById(Integer id);
}
