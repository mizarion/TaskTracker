package com.consist.taskboot.repository.specification;

import com.consist.taskboot.model.entity.TaskEntity;

import java.util.Objects;

public class TaskSpecificationById implements TaskSpecification {

    private final Integer id;

    public TaskSpecificationById(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public boolean specified(TaskEntity task) {
        return task.getId().equals(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskSpecificationById that = (TaskSpecificationById) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
