package com.consist.task.service;

import com.consist.task.component.TaskMapper;
import com.consist.task.model.dto.TaskDto;
import com.consist.task.model.entity.TaskEntity;
import com.consist.task.repository.TaskRepository;
import com.consist.task.repository.specification.TaskSpecificationById;
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
