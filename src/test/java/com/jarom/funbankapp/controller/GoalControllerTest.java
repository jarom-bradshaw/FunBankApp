package com.jarom.funbankapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jarom.funbankapp.dto.GoalDTO;
import com.jarom.funbankapp.model.Goal;
import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.GoalRepository;
import com.jarom.funbankapp.repository.UserRepository;
import com.jarom.funbankapp.security.JwtAuthFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = GoalController.class,
        excludeFilters = @Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthFilter.class}
        ),
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class}
)
public class GoalControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GoalRepository goalRepository;

    @MockBean
    private UserRepository userRepository;

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
        testGoal.setCreatedAt(Timestamp.valueOf(LocalDateTime.of(2024, 1, 1, 12, 0, 0)));
        testGoal.setUpdatedAt(Timestamp.valueOf(LocalDateTime.of(2024, 1, 1, 12, 0, 0)));

        testGoals = Arrays.asList(testGoal);
    }

    @Test
    @WithMockUser(username = "testuser")
    void createGoal_ShouldCreateSuccessfully() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        GoalDTO request = new GoalDTO();
        request.setName("New Goal");
        request.setDescription("Test goal");
        request.setTargetAmount(new BigDecimal("1000.00"));

        mockMvc.perform(post("/api/goals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Goal created successfully"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getUserGoals_ShouldReturnGoals() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(goalRepository.findByUserId(1L)).thenReturn(testGoals);

        mockMvc.perform(get("/api/goals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("Save for vacation"))
                .andExpect(jsonPath("$.data[0].targetAmount").value(5000.00));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getGoal_ShouldReturnGoal_WhenGoalExists() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(goalRepository.findById(1L)).thenReturn(Optional.of(testGoal));

        mockMvc.perform(get("/api/goals/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Save for vacation"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getGoal_ShouldReturnNotFound_WhenGoalDoesNotExist() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(goalRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/goals/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("ERROR"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getGoal_ShouldReturnForbidden_WhenUserDoesNotOwnGoal() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        
        Goal otherUserGoal = new Goal();
        otherUserGoal.setId(1L);
        otherUserGoal.setUserId(999L); // Different user
        otherUserGoal.setName("Other user's goal");
        
        when(goalRepository.findById(1L)).thenReturn(Optional.of(otherUserGoal));

        mockMvc.perform(get("/api/goals/1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.error").value("Access denied"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateGoal_ShouldUpdateSuccessfully_WhenUserOwnsGoal() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(goalRepository.findById(1L)).thenReturn(Optional.of(testGoal));

        GoalDTO request = new GoalDTO();
        request.setName("Updated Goal");
        request.setDescription("Updated description");

        mockMvc.perform(put("/api/goals/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Goal updated successfully"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateGoal_ShouldReturnNotFound_WhenGoalDoesNotExist() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(goalRepository.findById(999L)).thenReturn(Optional.empty());

        GoalDTO request = new GoalDTO();
        request.setName("Updated Goal");

        mockMvc.perform(put("/api/goals/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("ERROR"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateGoal_ShouldReturnForbidden_WhenUserDoesNotOwnGoal() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        
        Goal otherUserGoal = new Goal();
        otherUserGoal.setId(1L);
        otherUserGoal.setUserId(999L); // Different user
        
        when(goalRepository.findById(1L)).thenReturn(Optional.of(otherUserGoal));

        GoalDTO request = new GoalDTO();
        request.setName("Updated Goal");

        mockMvc.perform(put("/api/goals/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.error").value("Access denied"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteGoal_ShouldDeleteSuccessfully_WhenUserOwnsGoal() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(goalRepository.findById(1L)).thenReturn(Optional.of(testGoal));

        mockMvc.perform(delete("/api/goals/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Goal deleted successfully"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteGoal_ShouldReturnNotFound_WhenGoalDoesNotExist() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(goalRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/goals/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("ERROR"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteGoal_ShouldReturnForbidden_WhenUserDoesNotOwnGoal() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        
        Goal otherUserGoal = new Goal();
        otherUserGoal.setId(1L);
        otherUserGoal.setUserId(999L); // Different user
        
        when(goalRepository.findById(1L)).thenReturn(Optional.of(otherUserGoal));

        mockMvc.perform(delete("/api/goals/1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.error").value("Access denied"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getGoalProgress_ShouldReturnProgress() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(goalRepository.findByUserId(1L)).thenReturn(testGoals);

        mockMvc.perform(get("/api/goals/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Goal summary retrieved successfully"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getGoalProgress_ShouldHandleEmptyGoals() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(goalRepository.findByUserId(1L)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/goals/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Goal summary retrieved successfully"));
    }
} 
