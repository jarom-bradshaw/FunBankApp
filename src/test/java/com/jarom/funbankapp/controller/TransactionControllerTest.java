package com.jarom.funbankapp.controller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jarom.funbankapp.dto.TransactionDTO;
import com.jarom.funbankapp.dto.TransactionRequest;
import com.jarom.funbankapp.security.JwtAuthFilter;
import com.jarom.funbankapp.service.TransactionService;

@WebMvcTest(
        controllers = TransactionController.class,
        excludeFilters = @Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthFilter.class}
        ),
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class}
)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionService transactionService;

    @Test
    @WithMockUser(username = "testuser")
    void testGetTransactions() throws Exception {
        // Arrange
        TransactionDTO transaction = new TransactionDTO();
        transaction.setId(1L);
        transaction.setAccountId(1L);
        transaction.setType("deposit");
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setCategory("Salary");
        transaction.setDescription("Monthly salary");
        transaction.setTransactionDate(new Timestamp(System.currentTimeMillis()));
        transaction.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        List<TransactionDTO> transactions = Arrays.asList(transaction);
        when(transactionService.getRecentTransactions("testuser", 100)).thenReturn(transactions);

        // Act & Assert
        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Transactions retrieved successfully"))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].type").value("deposit"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetTransactionsWithLimit() throws Exception {
        // Arrange
        TransactionDTO transaction = new TransactionDTO();
        transaction.setId(1L);
        transaction.setAccountId(1L);
        transaction.setType("deposit");
        transaction.setAmount(new BigDecimal("100.00"));

        List<TransactionDTO> transactions = Arrays.asList(transaction);
        when(transactionService.getRecentTransactions("testuser", 10)).thenReturn(transactions);

        // Act & Assert
        mockMvc.perform(get("/api/transactions?limit=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Transactions retrieved successfully"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testCreateTransaction() throws Exception {
        // Arrange
        TransactionRequest request = new TransactionRequest();
        request.setAccountId(1L);
        request.setType("deposit");
        request.setAmount(new BigDecimal("100.00"));
        request.setCategory("Salary");
        request.setDescription("Monthly salary");

        TransactionDTO response = new TransactionDTO();
        response.setId(1L);
        response.setAccountId(1L);
        response.setType("deposit");
        response.setAmount(new BigDecimal("100.00"));
        response.setCategory("Salary");
        response.setDescription("Monthly salary");

        when(transactionService.createTransaction(anyString(), any(TransactionRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Transaction created successfully"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testDeposit() throws Exception {
        // Arrange
        TransactionRequest request = new TransactionRequest();
        request.setAccountId(1L);
        request.setAmount(new BigDecimal("100.00"));
        request.setCategory("Salary");
        request.setDescription("Monthly salary");
        request.setType("deposit"); // Set type to pass validation

        TransactionDTO response = new TransactionDTO();
        response.setId(1L);
        response.setAccountId(1L);
        response.setType("deposit");
        response.setAmount(new BigDecimal("100.00"));

        when(transactionService.createTransaction(anyString(), any(TransactionRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/transactions/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Deposit successful"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testWithdraw() throws Exception {
        // Arrange
        TransactionRequest request = new TransactionRequest();
        request.setAccountId(1L);
        request.setAmount(new BigDecimal("50.00"));
        request.setCategory("Food");
        request.setDescription("Lunch");
        request.setType("withdraw"); // Set type to pass validation

        TransactionDTO response = new TransactionDTO();
        response.setId(1L);
        response.setAccountId(1L);
        response.setType("withdraw");
        response.setAmount(new BigDecimal("50.00"));

        when(transactionService.createTransaction(anyString(), any(TransactionRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/transactions/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Withdrawal successful"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetTransactionsByAccount() throws Exception {
        // Arrange
        TransactionDTO transaction = new TransactionDTO();
        transaction.setId(1L);
        transaction.setAccountId(1L);
        transaction.setType("deposit");
        transaction.setAmount(new BigDecimal("100.00"));

        List<TransactionDTO> transactions = Arrays.asList(transaction);
        when(transactionService.getTransactionsByAccount("testuser", 1L)).thenReturn(transactions);

        // Act & Assert
        mockMvc.perform(get("/api/transactions/account/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Account transactions retrieved successfully"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetSpendingByCategory() throws Exception {
        // Arrange
        Map<String, BigDecimal> categories = new HashMap<>();
        categories.put("Food", new BigDecimal("150.00"));
        categories.put("Transport", new BigDecimal("50.00"));

        when(transactionService.getSpendingByCategory("testuser", 30)).thenReturn(categories);

        // Act & Assert
        mockMvc.perform(get("/api/transactions/spending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Spending by category retrieved successfully"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetTransactionSummary() throws Exception {
        // Arrange
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalSpent", new BigDecimal("500.00"));
        summary.put("totalIncome", new BigDecimal("1000.00"));
        summary.put("netAmount", new BigDecimal("500.00"));

        when(transactionService.getTransactionSummary("testuser", 30)).thenReturn(summary);

        // Act & Assert
        mockMvc.perform(get("/api/transactions/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Transaction summary retrieved successfully"));
    }
} 