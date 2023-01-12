package com.consist.task.h2.config;

import com.consist.task.component.ParameterMapper;
import com.consist.task.component.ParameterMapperImpl;
import com.consist.task.component.TaskMapper;
import com.consist.task.component.TaskMapperImpl;
import com.consist.task.config.Schema;
import com.consist.task.controller.TaskController;
import com.consist.task.repository.TaskRepository;
import com.consist.task.repository.TaskRepositoryJdbc;
import com.consist.task.repository.TaskRepositoryJdbcTemplate;
import com.consist.task.service.TaskService;
import com.consist.task.service.TaskServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class H2Config {

    @Bean
    DataSource dataSource(Environment environment) {
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();

        String url = environment.getProperty("url");
        String username = environment.getProperty("username");
        String password = environment.getProperty("password");
        String driver = environment.getProperty("driver");

        if (url == null || username == null || password == null || driver == null) {
            driverManagerDataSource.setUrl("jdbc:h2:mem:consistdb;DB_CLOSE_DELAY=-1");
            driverManagerDataSource.setUsername("username");
            driverManagerDataSource.setPassword("password");
            driverManagerDataSource.setDriverClassName("org.h2.Driver");
        } else {
            driverManagerDataSource.setUrl(url);
            driverManagerDataSource.setUsername(username);
            driverManagerDataSource.setPassword(password);
            driverManagerDataSource.setDriverClassName(driver);
        }
        return driverManagerDataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public TaskMapper taskMapper() {
        return new TaskMapperImpl();
    }

    @Bean
    public ParameterMapper parameterMapper() {
        return new ParameterMapperImpl();
    }

    @Bean
    public Schema schema() {
        return new Schema("classpath:schema.sql");
    }

    @Bean
    @Primary
    public TaskRepository taskRepositoryImpl(DataSource dataSource, TaskMapper taskMapper, ParameterMapper parameterMapper, Schema schemaSql) {
        return new TaskRepositoryJdbc(dataSource, taskMapper, parameterMapper, schemaSql);
    }

    @Bean
    public TaskRepository taskRepositoryJdbcTemplate(JdbcTemplate jdbcTemplate, TaskMapper taskMapper, ParameterMapper parameterMapper, Schema schemaSql) {
        return new TaskRepositoryJdbcTemplate(jdbcTemplate, taskMapper, parameterMapper, schemaSql);
    }

    @Bean
    public TaskService taskService(TaskRepository taskRepository, TaskMapper taskMapper) {
        return new TaskServiceImpl(taskRepository, taskMapper);
    }

    @Bean
    public TaskController taskController(TaskService taskService, TaskMapper taskMapper) {
        return new TaskController(taskService, taskMapper);
    }

}
