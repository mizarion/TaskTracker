package com.consist.task.repository.specification;

import com.consist.task.model.entity.TaskEntity;

public interface TaskSpecification {
    boolean specified(TaskEntity account);
}