package com.consist.task.resttemplate;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URL;

@Configuration
public class RestTemplateConfig {

    @Bean
    URL url(Environment environment) throws MalformedURLException {
        String address = environment.getProperty("address");
        return new URL(address != null ? address : "http://localhost:8080/tasks");
    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
