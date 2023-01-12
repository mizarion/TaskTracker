package com.consist.task.repository;

import com.consist.task.model.entity.TaskEntity;
import com.consist.task.repository.specification.TaskSpecification;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@Repository
public class TaskRepositoryMapImpl implements TaskRepository {

    private final Map<Integer, TaskEntity> storage;

    public TaskRepositoryMapImpl() {
        storage = new HashMap<>();
    }

    @Override
    public List<TaskEntity> query(TaskSpecification taskSpecification) {
        List<TaskEntity> list = new ArrayList<>();
        for (Entry<Integer, TaskEntity> entry : storage.entrySet()) {
            if (taskSpecification.specified(storage.get(entry.getKey()))) {
                list.add(entry.getValue());
            }
        }
        return list;
    }

    @Override
    public void create(TaskEntity taskEntity) {
        if (storage.put(taskEntity.getId(), taskEntity) != null) {
            throw new IllegalArgumentException("task with id=" + taskEntity.getId() + " already exist");
        }
    }

    @Override
    public void update(TaskEntity taskEntity) {
        if (!storage.containsKey(taskEntity.getId())) {
            throw new IllegalArgumentException("there are no task with id=" + taskEntity.getId());
        }
        storage.put(taskEntity.getId(), taskEntity);
    }

    @Override
    public void deleteById(Integer id) {
        if (storage.remove(id) == null) {
            throw new IllegalArgumentException("there are no task with id=" + id);
        }
    }
}
