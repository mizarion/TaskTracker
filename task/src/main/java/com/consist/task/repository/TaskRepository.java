package com.consist.task.repository;

import com.consist.task.model.entity.TaskEntity;
import com.consist.task.repository.specification.TaskSpecification;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TaskRepository {

    void create(TaskEntity taskEntity) ;

    void update(TaskEntity taskEntity) ;

    void deleteById(Integer id) ;

    List<TaskEntity> query(TaskSpecification taskSpecification) ;
}
