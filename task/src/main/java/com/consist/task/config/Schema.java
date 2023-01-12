package com.consist.task.config;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Schema {

    private final Resource resource;

    public Schema(Resource resource) {
        this.resource = resource;
    }

    public Schema(String path) {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        this.resource = resourceLoader.getResource(path);
    }

    @Override
    public String toString() {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}