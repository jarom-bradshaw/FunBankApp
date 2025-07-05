package com.jarom.funbankapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jarom.funbankapp.model.Goal;
import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.GoalDAO;
import com.jarom.funbankapp.repository.UserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GoalController.class)
@TestPropertySource(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
})
public class GoalControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GoalDAO goalDAO;

    @MockBean
    private UserDAO userDAO;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private Goal testGoal;
    private List<Goal> testGoals;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        testGoal = new Goal();
        testGoal.setId(1L);
        testGoal.setUserId(1L);
        testGoal.setName("Save for vacation");
        testGoal.setDescription("Save money for summer vacation");
        testGoal.setTargetAmount(new BigDecimal("5000.00"));
        testGoal.setCurrentAmount(new BigDecimal("1000.00"));
        testGoal.setStatus("active");
        testGoal.setCreatedAt(LocalDateTime.of(2024, 1, 1, 12, 0, 0));
        testGoal.setUpdatedAt(LocalDateTime.of(2024, 1, 1, 12, 0, 0));

        testGoals = Arrays.asList(testGoal);
    }

    @Test
    @WithMockUser(username = "testuser")
    void createGoal_ShouldCreateSuccessfully() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);

        Goal request = new Goal();
        request.setName("New Goal");
        request.setDescription("Test goal");
        request.setTargetAmount(new BigDecimal("1000.00"));

        mockMvc.perform(post("/api/goals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Goal created successfully!"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getUserGoals_ShouldReturnGoals() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);
        when(goalDAO.findByUserId(1L)).thenReturn(testGoals);

        mockMvc.perform(get("/api/goals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").exists())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Save for vacation"))
                .andExpect(jsonPath("$[0].targetAmount").value(5000.00));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getGoal_ShouldReturnGoal_WhenGoalExists() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);
        when(goalDAO.findById(1L)).thenReturn(testGoal);

        mockMvc.perform(get("/api/goals/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Save for vacation"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getGoal_ShouldReturnNotFound_WhenGoalDoesNotExist() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);
        when(goalDAO.findById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/goals/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getGoal_ShouldReturnForbidden_WhenUserDoesNotOwnGoal() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);
        
        Goal otherUserGoal = new Goal();
        otherUserGoal.setId(1L);
        otherUserGoal.setUserId(999L); // Different user
        otherUserGoal.setName("Other user's goal");
        
        when(goalDAO.findById(1L)).thenReturn(otherUserGoal);

        mockMvc.perform(get("/api/goals/1"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Unauthorized: You don't own this goal."));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateGoal_ShouldUpdateSuccessfully_WhenUserOwnsGoal() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);
        when(goalDAO.findById(1L)).thenReturn(testGoal);

        Goal request = new Goal();
        request.setName("Updated Goal");
        request.setDescription("Updated description");

        mockMvc.perform(put("/api/goals/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Goal updated successfully!"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateGoal_ShouldReturnNotFound_WhenGoalDoesNotExist() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);
        when(goalDAO.findById(999L)).thenReturn(null);

        Goal request = new Goal();
        request.setName("Updated Goal");

        mockMvc.perform(put("/api/goals/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateGoal_ShouldReturnForbidden_WhenUserDoesNotOwnGoal() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);
        
        Goal otherUserGoal = new Goal();
        otherUserGoal.setId(1L);
        otherUserGoal.setUserId(999L); // Different user
        
        when(goalDAO.findById(1L)).thenReturn(otherUserGoal);

        Goal request = new Goal();
        request.setName("Updated Goal");

        mockMvc.perform(put("/api/goals/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Unauthorized: You don't own this goal."));
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteGoal_ShouldDeleteSuccessfully_WhenUserOwnsGoal() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);
        when(goalDAO.findById(1L)).thenReturn(testGoal);

        mockMvc.perform(delete("/api/goals/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Goal deleted successfully!"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteGoal_ShouldReturnNotFound_WhenGoalDoesNotExist() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);
        when(goalDAO.findById(999L)).thenReturn(null);

        mockMvc.perform(delete("/api/goals/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteGoal_ShouldReturnForbidden_WhenUserDoesNotOwnGoal() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);
        
        Goal otherUserGoal = new Goal();
        otherUserGoal.setId(1L);
        otherUserGoal.setUserId(999L); // Different user
        
        when(goalDAO.findById(1L)).thenReturn(otherUserGoal);

        mockMvc.perform(delete("/api/goals/1"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Unauthorized: You don't own this goal."));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getGoalProgress_ShouldReturnProgress() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);
        when(goalDAO.findByUserId(1L)).thenReturn(testGoals);

        mockMvc.perform(get("/api/goals/progress"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalTarget").value(5000.00))
                .andExpect(jsonPath("$.totalCurrent").value(1000.00))
                .andExpect(jsonPath("$.overallProgress").value(20.0000))
                .andExpect(jsonPath("$.goals").isArray());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getGoalProgress_ShouldHandleEmptyGoals() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);
        when(goalDAO.findByUserId(1L)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/goals/progress"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalTarget").value("0"))
                .andExpect(jsonPath("$.totalCurrent").value("0"))
                .andExpect(jsonPath("$.overallProgress").value("0"));
    }
} 