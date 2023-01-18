package com.consist.taskboot.repository.specification;

import com.consist.taskboot.model.entity.TaskEntity;

public interface TaskSpecification {
    boolean specified(TaskEntity account);
}