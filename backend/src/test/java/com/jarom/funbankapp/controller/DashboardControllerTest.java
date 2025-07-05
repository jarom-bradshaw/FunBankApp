package com.jarom.funbankapp.controller;

import com.jarom.funbankapp.model.Account;
import com.jarom.funbankapp.model.Budget;
import com.jarom.funbankapp.model.Transaction;
import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.AccountDAO;
import com.jarom.funbankapp.repository.BudgetDAO;
import com.jarom.funbankapp.repository.TransactionDAO;
import com.jarom.funbankapp.repository.UserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardController.class)
@TestPropertySource(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
})
public class DashboardControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDAO userDAO;

    @MockBean
    private AccountDAO accountDAO;

    @MockBean
    private TransactionDAO transactionDAO;

    @MockBean
    private BudgetDAO budgetDAO;

    private User testUser;
    private Account testAccount;
    private Transaction testTransaction;
    private Budget testBudget;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        testAccount = new Account();
        testAccount.setId(1L);
        testAccount.setUserId(1L);
        testAccount.setName("Checking Account");
        testAccount.setBalance(new BigDecimal("5000.00"));
        testAccount.setType("checking");

        testTransaction = new Transaction();
        testTransaction.setId(1L);
        testTransaction.setAccountId(1L);
        testTransaction.setType("expense");
        testTransaction.setAmount(new BigDecimal("100.00"));
        testTransaction.setDescription("Test transaction");
        testTransaction.setCreatedAt(Timestamp.valueOf(LocalDateTime.of(2024, 1, 1, 12, 0, 0)));

        testBudget = new Budget();
        testBudget.setId(1L);
        testBudget.setUserId(1L);
        testBudget.setName("Monthly Groceries");
        testBudget.setAmount(new BigDecimal("500.00"));
        testBudget.setCategory("Food");
    }

    @Test
    @WithMockUser(username = "testuser")
    void getDashboard_ShouldReturnDashboardData() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);
        when(accountDAO.findByUserId(1L)).thenReturn(Arrays.asList(testAccount));
        when(transactionDAO.getRecentTransactions(1L, 10)).thenReturn(Arrays.asList(testTransaction));
        when(budgetDAO.findByUserId(1L)).thenReturn(Arrays.asList(testBudget));
        when(transactionDAO.getSpendingByCategory(1L, 30)).thenReturn(new HashMap<>());

        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalBalance").value(5000.00))
                .andExpect(jsonPath("$.accounts").isArray())
                .andExpect(jsonPath("$.recentTransactions").isArray())
                .andExpect(jsonPath("$.budgets").isArray())
                .andExpect(jsonPath("$.totalBudgeted").value(500.00))
                .andExpect(jsonPath("$.spendingByCategory").isMap());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getDashboard_ShouldHandleEmptyData() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);
        when(accountDAO.findByUserId(1L)).thenReturn(Arrays.asList());
        when(transactionDAO.getRecentTransactions(1L, 10)).thenReturn(Arrays.asList());
        when(budgetDAO.findByUserId(1L)).thenReturn(Arrays.asList());
        when(transactionDAO.getSpendingByCategory(1L, 30)).thenReturn(new HashMap<>());

        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalBalance").value(0))
                .andExpect(jsonPath("$.totalBudgeted").value(0));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getAccountSummary_ShouldReturnAccountSummary() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);
        when(accountDAO.findByUserId(1L)).thenReturn(Arrays.asList(testAccount));

        mockMvc.perform(get("/api/dashboard/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accounts").isArray())
                .andExpect(jsonPath("$.totalBalance").value(5000.00));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getAccountSummary_ShouldHandleEmptyAccounts() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);
        when(accountDAO.findByUserId(1L)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/dashboard/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalBalance").value("0"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getRecentTransactions_ShouldReturnTransactions() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);
        when(transactionDAO.getRecentTransactions(1L, 10)).thenReturn(Arrays.asList(testTransaction));

        mockMvc.perform(get("/api/dashboard/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].amount").value(100.00));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getRecentTransactions_ShouldUseCustomLimit() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);
        when(transactionDAO.getRecentTransactions(1L, 5)).thenReturn(Arrays.asList(testTransaction));

        mockMvc.perform(get("/api/dashboard/transactions")
                .param("limit", "5"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getRecentTransactions_ShouldUseDefaultLimit() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);
        when(transactionDAO.getRecentTransactions(1L, 10)).thenReturn(Arrays.asList(testTransaction));

        mockMvc.perform(get("/api/dashboard/transactions"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getSpendingAnalysis_ShouldReturnSpendingByCategory() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);
        
        Map<String, BigDecimal> spendingByCategory = new HashMap<>();
        spendingByCategory.put("Food", new BigDecimal("200.00"));
        spendingByCategory.put("Transportation", new BigDecimal("150.00"));
        
        when(transactionDAO.getSpendingByCategory(1L, 30)).thenReturn(spendingByCategory);

        mockMvc.perform(get("/api/dashboard/spending")
                .param("days", "30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Food").value(200.00))
                .andExpect(jsonPath("$.Transportation").value(150.00));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getSpendingAnalysis_ShouldUseDefaultDays() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);
        when(transactionDAO.getSpendingByCategory(1L, 30)).thenReturn(new HashMap<>());

        mockMvc.perform(get("/api/dashboard/spending"))
                .andExpect(status().isOk());
    }
} 
