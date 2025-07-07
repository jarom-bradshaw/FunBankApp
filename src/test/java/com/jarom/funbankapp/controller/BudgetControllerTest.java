package com.jarom.funbankapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jarom.funbankapp.dto.BudgetDTO;
import com.jarom.funbankapp.model.Budget;
import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.BudgetRepository;
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
        controllers = BudgetController.class,
        excludeFilters = @Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthFilter.class}
        ),
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class}
)
public class BudgetControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BudgetRepository budgetRepository;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private Budget testBudget;
    private List<Budget> testBudgets;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        testBudget = new Budget();
        testBudget.setId(1L);
        testBudget.setUserId(1L);
        testBudget.setName("Monthly Groceries");
        testBudget.setDescription("Budget for monthly grocery expenses");
        testBudget.setAmount(new BigDecimal("500.00"));
        testBudget.setCategory("Food");
        testBudget.setPeriod("monthly");
        testBudget.setCreatedAt(Timestamp.valueOf(LocalDateTime.of(2024, 1, 1, 12, 0, 0)));
        testBudget.setUpdatedAt(Timestamp.valueOf(LocalDateTime.of(2024, 1, 1, 12, 0, 0)));

        testBudgets = Arrays.asList(testBudget);
    }

    @Test
    @WithMockUser(username = "testuser")
    void createBudget_ShouldCreateSuccessfully() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        BudgetDTO request = new BudgetDTO();
        request.setName("New Budget");
        request.setDescription("Test budget");
        request.setAmount(new BigDecimal("300.00"));
        request.setCategory("Entertainment");

        mockMvc.perform(post("/api/budgets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Budget created successfully"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getUserBudgets_ShouldReturnBudgets() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(budgetRepository.findByUserId(1L)).thenReturn(testBudgets);

        mockMvc.perform(get("/api/budgets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("Monthly Groceries"))
                .andExpect(jsonPath("$.data[0].amount").value(500.00));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getBudget_ShouldReturnBudget_WhenBudgetExists() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(budgetRepository.findById(1L)).thenReturn(Optional.of(testBudget));

        mockMvc.perform(get("/api/budgets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Monthly Groceries"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getBudget_ShouldReturnNotFound_WhenBudgetDoesNotExist() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(budgetRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/budgets/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("ERROR"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getBudget_ShouldReturnForbidden_WhenUserDoesNotOwnBudget() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        
        Budget otherUserBudget = new Budget();
        otherUserBudget.setId(1L);
        otherUserBudget.setUserId(999L); // Different user
        otherUserBudget.setName("Other user's budget");
        
        when(budgetRepository.findById(1L)).thenReturn(Optional.of(otherUserBudget));

        mockMvc.perform(get("/api/budgets/1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.error").value("Access denied"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateBudget_ShouldUpdateSuccessfully_WhenUserOwnsBudget() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(budgetRepository.findById(1L)).thenReturn(Optional.of(testBudget));

        BudgetDTO request = new BudgetDTO();
        request.setName("Updated Budget");
        request.setDescription("Updated description");

        mockMvc.perform(put("/api/budgets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Budget updated successfully"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateBudget_ShouldReturnNotFound_WhenBudgetDoesNotExist() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(budgetRepository.findById(999L)).thenReturn(Optional.empty());

        BudgetDTO request = new BudgetDTO();
        request.setName("Updated Budget");

        mockMvc.perform(put("/api/budgets/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("ERROR"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateBudget_ShouldReturnForbidden_WhenUserDoesNotOwnBudget() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        
        Budget otherUserBudget = new Budget();
        otherUserBudget.setId(1L);
        otherUserBudget.setUserId(999L); // Different user
        
        when(budgetRepository.findById(1L)).thenReturn(Optional.of(otherUserBudget));

        BudgetDTO request = new BudgetDTO();
        request.setName("Updated Budget");

        mockMvc.perform(put("/api/budgets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.error").value("Access denied"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteBudget_ShouldDeleteSuccessfully_WhenUserOwnsBudget() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(budgetRepository.findById(1L)).thenReturn(Optional.of(testBudget));

        mockMvc.perform(delete("/api/budgets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Budget deleted successfully"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteBudget_ShouldReturnNotFound_WhenBudgetDoesNotExist() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(budgetRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/budgets/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("ERROR"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteBudget_ShouldReturnForbidden_WhenUserDoesNotOwnBudget() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        
        Budget otherUserBudget = new Budget();
        otherUserBudget.setId(1L);
        otherUserBudget.setUserId(999L); // Different user
        
        when(budgetRepository.findById(1L)).thenReturn(Optional.of(otherUserBudget));

        mockMvc.perform(delete("/api/budgets/1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.error").value("Access denied"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getBudgetSummary_ShouldReturnSummary() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(budgetRepository.findByUserId(1L)).thenReturn(testBudgets);

        mockMvc.perform(get("/api/budgets/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Budget summary retrieved successfully"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getBudgetSummary_ShouldHandleEmptyBudgets() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(budgetRepository.findByUserId(1L)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/budgets/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Budget summary retrieved successfully"));
    }
} 
