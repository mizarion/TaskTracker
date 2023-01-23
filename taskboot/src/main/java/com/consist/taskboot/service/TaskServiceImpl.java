package com.consist.taskboot.service;

import com.consist.taskboot.component.mapper.TaskMapper;
import com.consist.taskboot.model.dto.TaskDto;
import com.consist.taskboot.model.entity.TaskEntity;
import com.consist.taskboot.repository.TaskRepository;
import com.consist.taskboot.repository.specification.TaskSpecificationById;
import org.springframework.stereotype.Service;


@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    private final TaskMapper taskMapper;

    public TaskServiceImpl(TaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    @Override
    public TaskDto findById(Integer id) {
        return taskMapper.mapToTaskDto(taskRepository.query(new TaskSpecificationById(id)).get(0));
    }

    @Override
    public void create(TaskEntity taskDto) {
        taskRepository.create(taskDto);
    }

    @Override
    public void update(TaskEntity taskDto) {
        taskRepository.update(taskDto);
    }

    @Override
    public void deleteById(Integer id) {
        taskRepository.deleteById(id);
    }
}
