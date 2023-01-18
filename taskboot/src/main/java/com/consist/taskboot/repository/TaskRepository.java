package com.consist.taskboot.repository;

import com.consist.taskboot.model.entity.TaskEntity;
import com.consist.taskboot.repository.specification.TaskSpecification;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TaskRepository {

    void create(TaskEntity taskEntity) ;

    void update(TaskEntity taskEntity) ;

    void deleteById(Integer id) ;

    List<TaskEntity> query(TaskSpecification taskSpecification) ;
}
