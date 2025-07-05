package com.jarom.funbankapp.controller;

import com.jarom.funbankapp.model.Account;
import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.AccountDAO;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AnalyticsController.class)
@TestPropertySource(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
})
public class AnalyticsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDAO userDAO;

    @MockBean
    private TransactionDAO transactionDAO;

    @MockBean
    private AccountDAO accountDAO;

    private User testUser;
    private Account testAccount;

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
    }

    @Test
    @WithMockUser(username = "testuser")
    void getSpendingAnalytics_ShouldReturnAnalytics() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);
        
        Map<String, BigDecimal> spendingByCategory = new HashMap<>();
        spendingByCategory.put("Food", new BigDecimal("200.00"));
        spendingByCategory.put("Transportation", new BigDecimal("150.00"));
        
        when(transactionDAO.getSpendingByCategory(1L, 30)).thenReturn(spendingByCategory);

        mockMvc.perform(get("/api/analytics/spending")
                .param("days", "30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalSpent").value(350.00))
                .andExpect(jsonPath("$.spendingByCategory.Food").value(200.00))
                .andExpect(jsonPath("$.spendingByCategory.Transportation").value(150.00))
                .andExpect(jsonPath("$.period").value("30 days"))
                .andExpect(jsonPath("$.averageDailySpending").value(11.67));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getSpendingAnalytics_ShouldUseDefaultDays() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);
        when(transactionDAO.getSpendingByCategory(1L, 30)).thenReturn(new HashMap<>());

        mockMvc.perform(get("/api/analytics/spending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.period").value("30 days"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getIncomeAnalytics_ShouldReturnAnalytics() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);

        mockMvc.perform(get("/api/analytics/income")
                .param("days", "30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalIncome").value("0"))
                .andExpect(jsonPath("$.incomeByCategory").isMap())
                .andExpect(jsonPath("$.period").value("30 days"))
                .andExpect(jsonPath("$.averageDailyIncome").value("0"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getIncomeAnalytics_ShouldUseDefaultDays() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);

        mockMvc.perform(get("/api/analytics/income"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.period").value("30 days"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getNetWorthAnalytics_ShouldReturnAnalytics() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);
        
        List<Account> accounts = Arrays.asList(testAccount);
        when(accountDAO.findByUserId(1L)).thenReturn(accounts);

        mockMvc.perform(get("/api/analytics/net-worth"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAssets").value(5000.00))
                .andExpect(jsonPath("$.totalLiabilities").value(0))
                .andExpect(jsonPath("$.netWorth").value(5000.00))
                .andExpect(jsonPath("$.accountCount").value(1));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getNetWorthAnalytics_ShouldHandleEmptyAccounts() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);
        when(accountDAO.findByUserId(1L)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/analytics/net-worth"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAssets").value("0"))
                .andExpect(jsonPath("$.totalLiabilities").value("0"))
                .andExpect(jsonPath("$.netWorth").value("0"))
                .andExpect(jsonPath("$.accountCount").value(0));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getCashFlowAnalytics_ShouldReturnAnalytics() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);

        mockMvc.perform(get("/api/analytics/cash-flow")
                .param("days", "30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.inflow").value("0"))
                .andExpect(jsonPath("$.outflow").value("0"))
                .andExpect(jsonPath("$.netCashFlow").value("0"))
                .andExpect(jsonPath("$.period").value("30 days"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getCashFlowAnalytics_ShouldUseDefaultDays() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);

        mockMvc.perform(get("/api/analytics/cash-flow"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.period").value("30 days"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getInvestmentPerformance_ShouldReturnAnalytics() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);

        mockMvc.perform(get("/api/analytics/investment-performance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalInvested").value("0"))
                .andExpect(jsonPath("$.currentValue").value("0"))
                .andExpect(jsonPath("$.totalReturn").value("0"))
                .andExpect(jsonPath("$.returnPercentage").value("0"))
                .andExpect(jsonPath("$.portfolioDiversification").isMap());
    }
} 