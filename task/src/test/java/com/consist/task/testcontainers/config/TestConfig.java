//package com.consist.task.testcontainers.config;
//
//import com.consist.task.component.ParameterMapper;
//import com.consist.task.component.ParameterMapperImpl;
//import com.consist.task.component.TaskMapper;
//import com.consist.task.component.TaskMapperImpl;
//import com.consist.task.controller.TaskController;
//import com.consist.task.repository.TaskRepository;
//import com.consist.task.repository.TaskRepositoryJdbcTemplate;
//import com.consist.task.service.TaskService;
//import com.consist.task.service.TaskServiceImpl;
//import org.postgresql.ds.PGSimpleDataSource;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.testcontainers.containers.PostgreSQLContainer;
//
//import javax.sql.DataSource;
//
//@Configuration
//public class TestConfig {
//
//    @Bean
//    public PostgreSQLContainer postgreSQLContainer() {
//        PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:11.8");
//        postgreSQLContainer.withDatabaseName("consistdb");
//        postgreSQLContainer.start();
//        return postgreSQLContainer;
//    }
//
//    @Bean
//    public DataSource dataSource(PostgreSQLContainer postgreSQLContainer) {
//        PGSimpleDataSource dataSource = new PGSimpleDataSource();
//        dataSource.setUrl(postgreSQLContainer.getJdbcUrl());
//        dataSource.setUser(postgreSQLContainer.getUsername());
//        dataSource.setPassword(postgreSQLContainer.getPassword());
//        return dataSource;
//    }
//
//    @Bean
//    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
//        return new JdbcTemplate(dataSource);
//    }
//
//    @Bean
//    public TaskMapper taskMapper() {
//        return new TaskMapperImpl();
//    }
//
//    @Bean
//    public ParameterMapper parameterMapper() {
//        return new ParameterMapperImpl();
//    }
//
//    @Bean
//    public TaskRepository taskRepository(JdbcTemplate jdbcTemplate, TaskMapper taskMapper, ParameterMapper parameterMapper) {
//        return new TaskRepositoryJdbcTemplate(jdbcTemplate, taskMapper, parameterMapper);
//    }
//
//    @Bean
//    public TaskService taskService(TaskRepository taskRepository, TaskMapper taskMapper) {
//        return new TaskServiceImpl(taskRepository, taskMapper);
//    }
//
//    @Bean
//    public TaskController taskController(TaskService taskService, TaskMapper taskMapper) {
//        return new TaskController(taskService, taskMapper);
//    }
//
//}
