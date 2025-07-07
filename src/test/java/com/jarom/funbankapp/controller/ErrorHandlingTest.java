package com.jarom.funbankapp.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ErrorHandlingTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testHealthEndpoint_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"));
    }

    @Test
    void testUnauthorizedEndpoint_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/auth/profile"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.status").value(401));
    }
} 