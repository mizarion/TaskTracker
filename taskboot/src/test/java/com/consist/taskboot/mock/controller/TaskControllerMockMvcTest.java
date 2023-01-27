package com.consist.taskboot.mock.controller;

import com.consist.taskboot.model.Status;
import com.consist.taskboot.model.dto.TaskDto;
import com.consist.taskboot.service.TaskService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerMockMvcTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TaskService taskServiceMock;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final TaskDto taskDto = new TaskDto(1, Status.READY, "task1");

    @Test
    void getTask() throws Exception {
        Mockito.when(taskServiceMock.findById(taskDto.getId())).thenReturn(taskDto);

        mockMvc.perform(get("/tasks/?id=" + taskDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskDto.getId()))
                .andExpect(jsonPath("$.status").value(taskDto.getStatus().name()))
                .andExpect(jsonPath("$.taskName").value(taskDto.getTaskName()));
    }

    @Test
    void createTask() throws Exception {
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto))
                )
                .andExpect(status().isAccepted());
    }

    @Test
    void updateTask() throws Exception {
        mockMvc.perform(put("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto))
                )
                .andExpect(status().isAccepted());
    }

    @Test
    void deleteTask() throws Exception {
        mockMvc.perform(delete("/tasks/?id=" + taskDto.getId()))
                .andExpect(status().isAccepted());
    }
}
