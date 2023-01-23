package com.consist.taskboot.controller;

import com.consist.taskboot.component.mapper.TaskMapper;

import com.consist.taskboot.model.dto.TaskDto;
import com.consist.taskboot.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    public TaskController(TaskService taskService, TaskMapper taskMapper) {
        this.taskService = taskService;
        this.taskMapper = taskMapper;
    }

    @GetMapping()
    public ResponseEntity<TaskDto> getTaskById(@RequestParam Integer id) {
        return ResponseEntity.ok(taskService.findById(id));
    }

    @PostMapping()
    public ResponseEntity<Void> postTask(@RequestBody TaskDto taskDto) {
        taskService.create(taskMapper.mapToTaskEntity(taskDto));
        return ResponseEntity.accepted().build();
    }

    @PutMapping()
    public ResponseEntity<Void> updateTask(@RequestBody TaskDto taskDto) {
        taskService.update(taskMapper.mapToTaskEntity(taskDto));
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping()
    public ResponseEntity<Void> deleteTask(@RequestParam Integer id) {
        taskService.deleteById(id);
        return ResponseEntity.accepted().build();
    }
}
