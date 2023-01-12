package com.consist.task.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;


@Configuration
@PropertySource("classpath:application.properties")
public class JdbcConfig {

    private final Environment environment;

    public JdbcConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    DataSource dataSource() {
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
        driverManagerDataSource.setUrl(environment.getProperty("spring.datasource.url"));
        driverManagerDataSource.setUsername(environment.getProperty("spring.datasource.username"));
        driverManagerDataSource.setPassword(environment.getProperty("spring.datasource.password"));
        driverManagerDataSource.setDriverClassName(environment.getProperty("spring.datasource.driver-class-name"));
        return driverManagerDataSource;
    }

    @Bean
    public JdbcTemplate applicationDataConnection() {
        return new JdbcTemplate(dataSource());
    }

    @Bean
    public Schema schema() {
        return new Schema("classpath:schema.sql");
    }
}
