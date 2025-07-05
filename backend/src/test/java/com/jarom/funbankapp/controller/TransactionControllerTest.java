package com.jarom.funbankapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jarom.funbankapp.model.Transaction;
import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.TransactionDAO;
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
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
@TestPropertySource(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
})
public class TransactionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionDAO transactionDAO;

    @MockBean
    private UserDAO userDAO;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private Transaction testTransaction;
    private List<Transaction> testTransactions;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        testTransaction = new Transaction();
        testTransaction.setId(1L);
        testTransaction.setAccountId(1L);
        testTransaction.setType("expense");
        testTransaction.setAmount(new BigDecimal("100.00"));
        testTransaction.setDescription("Test transaction");
        testTransaction.setCreatedAt(Timestamp.valueOf(LocalDateTime.of(2024, 1, 1, 12, 0, 0)));

        testTransactions = Arrays.asList(testTransaction);
    }

    @Test
    @WithMockUser(username = "testuser")
    void getUserTransactions_ShouldReturnTransactions() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);
        when(transactionDAO.getRecentTransactions(1L, 100)).thenReturn(testTransactions);

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].amount").value(100.00))
                .andExpect(jsonPath("$[0].description").value("Test transaction"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void createTransaction_ShouldCreateSuccessfully() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);
        when(transactionDAO.logTransaction(anyLong(), anyString(), any(BigDecimal.class), anyString()))
                .thenReturn(1);

        Transaction request = new Transaction();
        request.setAccountId(1L);
        request.setType("expense");
        request.setAmount(new BigDecimal("50.00"));
        request.setDescription("New transaction");

        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Transaction created successfully!"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void createTransaction_ShouldReturnBadRequest_WhenCreationFails() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);
        when(transactionDAO.logTransaction(anyLong(), anyString(), any(BigDecimal.class), anyString()))
                .thenReturn(0);

        Transaction request = new Transaction();
        request.setAccountId(1L);
        request.setType("expense");
        request.setAmount(new BigDecimal("50.00"));
        request.setDescription("New transaction");

        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Failed to create transaction"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getTransaction_ShouldReturnTransactionDetails() throws Exception {
        mockMvc.perform(get("/api/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Transaction details for ID: 1"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateTransaction_ShouldReturnSuccessMessage() throws Exception {
        Transaction request = new Transaction();
        request.setDescription("Updated transaction");

        mockMvc.perform(put("/api/transactions/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Transaction updated successfully!"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteTransaction_ShouldReturnSuccessMessage() throws Exception {
        mockMvc.perform(delete("/api/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Transaction deleted successfully!"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void importTransactions_ShouldImportSuccessfully() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);
        when(transactionDAO.logTransaction(anyLong(), anyString(), any(BigDecimal.class), anyString()))
                .thenReturn(1);

        List<Transaction> transactions = Arrays.asList(testTransaction);

        mockMvc.perform(post("/api/transactions/import")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactions)))
                .andExpect(status().isOk())
                .andExpect(content().string("Imported 1 transactions successfully!"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getTransactionCategories_ShouldReturnCategories() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);
        
        Map<String, BigDecimal> categories = new HashMap<>();
        categories.put("Food", new BigDecimal("200.00"));
        categories.put("Transportation", new BigDecimal("150.00"));
        
        when(transactionDAO.getSpendingByCategory(1L, 30)).thenReturn(categories);

        mockMvc.perform(get("/api/transactions/categories")
                .param("days", "30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Food").value(200.00))
                .andExpect(jsonPath("$.Transportation").value(150.00));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getTransactionCategories_ShouldUseDefaultDays() throws Exception {
        when(userDAO.findByUsername("testuser")).thenReturn(testUser);
        when(transactionDAO.getSpendingByCategory(1L, 30)).thenReturn(new HashMap<>());

        mockMvc.perform(get("/api/transactions/categories"))
                .andExpect(status().isOk());
    }
} 
