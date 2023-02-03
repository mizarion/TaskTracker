package com.consist.taskboot.mock.controller;

import com.consist.taskboot.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerMockMvcJsonTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TaskService taskServiceMock;
    private static final String correctJsonFormat = """
            {
              "id": 40,
              "status": "READY",
              "name": "task40",
              "parameters": [
                {
                  "param_type": "int",
                  "param_name": "taskparam",
                  "param_value": "100"
                }
              ],
              "subtasks": [
                {
                  "id": 41,
                  "status": "READY",
                  "name": "subtask41",
                  "parameters": [
                    {
                        "param_type": "int",
                        "param_name": "taskparam",
                        "param_value": "100"
                    }
                  ],
                  "subtasks": []
                }
              ]
            }""";

    private static final String wrongJsonFormatNegativeId = """
            {
              "id": -10,
              "status": "READY",
              "name": "negative task",
              "parameters": [],
              "subtasks": []
            }""";
    private static final String wrongJsonFormatStatus = """
            {
              "id": 11,
              "status": "WRONG STATUS 123",
              "name": "negative task",
              "parameters": [],
              "subtasks": []
            }""";
    private static final String wrongJsonFormat = """
            {
              "id": 40,
              "status": "READY",
              "name": "task40",
              "parameters": [
                {
                  "type": "int",
                  "taskName": "taskparam",
                  "value": "100"
                }
              ],
              "subtasks": [
                {
                  "id": 41,
                  "status": "READY",
                  "name": "subtask41",
                  "parameters": [],
                  "subtasks": []
                }
              ]
            }""";
    private static final String wrongJsonFormatSubParam = """
            {
              "id": 40,
              "status": "READY",
              "name": "task40",
              "parameters": [
                {
                  "param_type": "int",
                  "param_name": "taskparam",
                  "param_value": "100"
                }
              ],
              "subtasks": [
                {
                  "id": 41,
                  "status": "READY",
                  "name": "subtask41",
                  "parameters": [
                    {
                        "WRONG PARAM 1": "int",
                        "WRONG PARAM 2": "taskparam",
                        "WRONG PARAM 3": "100"
                    }
                  ],
                  "subtasks": []
                }
              ]
            }""";


    @Test
    void createTaskJson() throws Exception {
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(correctJsonFormat)
                )
                .andExpect(status().isAccepted());
    }

    @Test
    void updateTaskJson() throws Exception {
        mockMvc.perform(put("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(correctJsonFormat)
                )
                .andExpect(status().isAccepted());
    }

    @Test
    void createTaskWrongJson() throws Exception {
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(wrongJsonFormat)
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    void updateTaskWrongJson() throws Exception {
        mockMvc.perform(put("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(wrongJsonFormat)
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createTaskWrongJsonSubParam() throws Exception {
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(wrongJsonFormatSubParam)
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    void updateTaskWrongJsonSubParam() throws Exception {
        mockMvc.perform(put("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(wrongJsonFormatSubParam)
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createTaskWrongJsonNegativeId() throws Exception {
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(wrongJsonFormatNegativeId)
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    void updateTaskWrongJsonNegativeId() throws Exception {
        mockMvc.perform(put("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(wrongJsonFormatNegativeId)
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createTaskWrongJsonStatus() throws Exception {
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(wrongJsonFormatStatus)
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    void updateTaskWrongJsonStatus() throws Exception {
        mockMvc.perform(put("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(wrongJsonFormatStatus)
                )
                .andExpect(status().is4xxClientError());
    }
}
