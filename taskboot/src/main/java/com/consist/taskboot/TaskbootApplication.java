package com.consist.taskboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class TaskbootApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(TaskbootApplication.class, args);
    }

}
