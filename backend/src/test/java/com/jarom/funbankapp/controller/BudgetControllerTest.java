package com.jarom.funbankapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jarom.funbankapp.model.Budget;
import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.BudgetDAO;
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
import java.time.LocalDateTime; import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BudgetController.class)
@TestPropertySource(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
})
public class BudgetControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BudgetDAO budgetDAO;

    @MockBean
    private UserDAO userDAO;

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
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);

        Budget request = new Budget();
        request.setName("New Budget");
        request.setDescription("Test budget");
        request.setAmount(new BigDecimal("300.00"));
        request.setCategory("Entertainment");

        mockMvc.perform(post("/api/budgets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Budget created successfully!"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getUserBudgets_ShouldReturnBudgets() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);
        when(budgetDAO.findByUserId(1L)).thenReturn(testBudgets);

        mockMvc.perform(get("/api/budgets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Monthly Groceries"))
                .andExpect(jsonPath("$[0].amount").value(500.00));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getBudget_ShouldReturnBudget_WhenBudgetExists() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);
        when(budgetDAO.findById(1L)).thenReturn(testBudget);

        mockMvc.perform(get("/api/budgets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Monthly Groceries"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getBudget_ShouldReturnNotFound_WhenBudgetDoesNotExist() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);
        when(budgetDAO.findById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/budgets/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getBudget_ShouldReturnForbidden_WhenUserDoesNotOwnBudget() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);
        
        Budget otherUserBudget = new Budget();
        otherUserBudget.setId(1L);
        otherUserBudget.setUserId(999L); // Different user
        otherUserBudget.setName("Other user's budget");
        
        when(budgetDAO.findById(1L)).thenReturn(otherUserBudget);

        mockMvc.perform(get("/api/budgets/1"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Unauthorized: You don't own this budget."));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateBudget_ShouldUpdateSuccessfully_WhenUserOwnsBudget() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);
        when(budgetDAO.findById(1L)).thenReturn(testBudget);

        Budget request = new Budget();
        request.setName("Updated Budget");
        request.setDescription("Updated description");

        mockMvc.perform(put("/api/budgets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Budget updated successfully!"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateBudget_ShouldReturnNotFound_WhenBudgetDoesNotExist() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);
        when(budgetDAO.findById(999L)).thenReturn(null);

        Budget request = new Budget();
        request.setName("Updated Budget");

        mockMvc.perform(put("/api/budgets/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateBudget_ShouldReturnForbidden_WhenUserDoesNotOwnBudget() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);
        
        Budget otherUserBudget = new Budget();
        otherUserBudget.setId(1L);
        otherUserBudget.setUserId(999L); // Different user
        
        when(budgetDAO.findById(1L)).thenReturn(otherUserBudget);

        Budget request = new Budget();
        request.setName("Updated Budget");

        mockMvc.perform(put("/api/budgets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Unauthorized: You don't own this budget."));
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteBudget_ShouldDeleteSuccessfully_WhenUserOwnsBudget() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);
        when(budgetDAO.findById(1L)).thenReturn(testBudget);

        mockMvc.perform(delete("/api/budgets/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Budget deleted successfully!"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteBudget_ShouldReturnNotFound_WhenBudgetDoesNotExist() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);
        when(budgetDAO.findById(999L)).thenReturn(null);

        mockMvc.perform(delete("/api/budgets/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteBudget_ShouldReturnForbidden_WhenUserDoesNotOwnBudget() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);
        
        Budget otherUserBudget = new Budget();
        otherUserBudget.setId(1L);
        otherUserBudget.setUserId(999L); // Different user
        
        when(budgetDAO.findById(1L)).thenReturn(otherUserBudget);

        mockMvc.perform(delete("/api/budgets/1"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Unauthorized: You don't own this budget."));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getBudgetSummary_ShouldReturnSummary() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);
        when(budgetDAO.findByUserId(1L)).thenReturn(testBudgets);

        mockMvc.perform(get("/api/budgets/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalBudgeted").value(500.00))
                .andExpect(jsonPath("$.budgets").isArray());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getBudgetSummary_ShouldHandleEmptyBudgets() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);
        when(budgetDAO.findByUserId(1L)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/budgets/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalBudgeted").value(0))
                .andExpect(jsonPath("$.budgets").isArray());
    }
} 
