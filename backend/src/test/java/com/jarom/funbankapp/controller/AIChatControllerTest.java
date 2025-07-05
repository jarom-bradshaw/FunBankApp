package com.jarom.funbankapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.UserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = AIChatController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class AIChatControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDAO userDAO;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
    }

    @Test
    @WithMockUser(username = "testuser")
    void chatWithAI_ShouldReturnResponse() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);

        AIChatController.ChatRequest request = new AIChatController.ChatRequest();
        request.setMessage("How can I save money?");

        mockMvc.perform(post("/api/ai/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @WithMockUser(username = "testuser")
    void chatWithAI_ShouldHandleBudgetQuestion() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);

        AIChatController.ChatRequest request = new AIChatController.ChatRequest();
        request.setMessage("I need help with budgeting");

        mockMvc.perform(post("/api/ai/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value(org.hamcrest.Matchers.containsString("budget")));
    }

    @Test
    @WithMockUser(username = "testuser")
    void chatWithAI_ShouldHandleSavingsQuestion() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);

        AIChatController.ChatRequest request = new AIChatController.ChatRequest();
        request.setMessage("How should I save money?");

        mockMvc.perform(post("/api/ai/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value(org.hamcrest.Matchers.containsStringIgnoringCase("sav")));
    }

    @Test
    @WithMockUser(username = "testuser")
    void chatWithAI_ShouldHandleInvestmentQuestion() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);

        AIChatController.ChatRequest request = new AIChatController.ChatRequest();
        request.setMessage("What should I invest in?");

        mockMvc.perform(post("/api/ai/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value(org.hamcrest.Matchers.containsString("invest")));
    }

    @Test
    @WithMockUser(username = "testuser")
    void chatWithAI_ShouldHandleDebtQuestion() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);

        AIChatController.ChatRequest request = new AIChatController.ChatRequest();
        request.setMessage("How do I manage my debt?");

        mockMvc.perform(post("/api/ai/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value(org.hamcrest.Matchers.containsString("debt")));
    }

    @Test
    @WithMockUser(username = "testuser")
    void chatWithAI_ShouldHandleGenericQuestion() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);

        AIChatController.ChatRequest request = new AIChatController.ChatRequest();
        request.setMessage("Hello, how are you?");

        mockMvc.perform(post("/api/ai/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").exists());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getRecommendations_ShouldReturnRecommendations() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);

        mockMvc.perform(get("/api/ai/recommendations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.budget").exists())
                .andExpect(jsonPath("$.savings").exists())
                .andExpect(jsonPath("$.emergency_fund").exists())
                .andExpect(jsonPath("$.investments").exists());
    }

    @Test
    @WithMockUser(username = "testuser")
    void analyzeSpendingPatterns_ShouldReturnAnalysis() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);

        mockMvc.perform(get("/api/ai/spending-patterns"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total_spent").value(0.0))
                .andExpect(jsonPath("$.top_categories").isArray())
                .andExpect(jsonPath("$.trend").exists())
                .andExpect(jsonPath("$.savings_potential").exists());
    }

    @Test
    @WithMockUser(username = "testuser")
    void detectAnomalies_ShouldReturnAnomalies() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);

        mockMvc.perform(get("/api/ai/anomaly-detection"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.detected").value(false))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.last_check").exists());
    }
} 