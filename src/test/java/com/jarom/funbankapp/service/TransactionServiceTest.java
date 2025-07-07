package com.jarom.funbankapp.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jarom.funbankapp.dto.TransactionDTO;
import com.jarom.funbankapp.dto.TransactionRequest;
import com.jarom.funbankapp.model.Transaction;
import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.TransactionRepository;
import com.jarom.funbankapp.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private User testUser;
    private Transaction testTransaction;
    private TransactionRequest testRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testTransaction = new Transaction();
        testTransaction.setId(1L);
        testTransaction.setAccountId(1L);
        testTransaction.setType("deposit");
        testTransaction.setAmount(new BigDecimal("100.00"));
        testTransaction.setCategory("Salary");
        testTransaction.setDescription("Monthly salary");
        testTransaction.setTransactionDate(new Timestamp(System.currentTimeMillis()));
        testTransaction.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        testRequest = new TransactionRequest();
        testRequest.setAccountId(1L);
        testRequest.setType("deposit");
        testRequest.setAmount(new BigDecimal("100.00"));
        testRequest.setCategory("Salary");
        testRequest.setDescription("Monthly salary");
    }

    @Test
    void testGetRecentTransactions_Success() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(transactionRepository.getRecentTransactions(1L, 10)).thenReturn(Arrays.asList(testTransaction));

        // Act
        List<TransactionDTO> result = transactionService.getRecentTransactions("testuser", 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTransaction.getId(), result.get(0).getId());
        assertEquals(testTransaction.getType(), result.get(0).getType());
        assertEquals(testTransaction.getAmount(), result.get(0).getAmount());
        
        verify(userRepository).findByUsername("testuser");
        verify(transactionRepository).getRecentTransactions(1L, 10);
    }

    @Test
    void testGetRecentTransactions_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            transactionService.getRecentTransactions("nonexistent", 10);
        });
        
        verify(userRepository).findByUsername("nonexistent");
        verify(transactionRepository, never()).getRecentTransactions(any(), anyInt());
    }

    @Test
    void testCreateTransaction_Success() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(transactionRepository.logTransaction(
            eq(1L), eq("deposit"), eq(new BigDecimal("100.00")), 
            eq("Salary"), eq("Monthly salary"), any(Timestamp.class)
        )).thenReturn(1);

        // Act
        TransactionDTO result = transactionService.createTransaction("testuser", testRequest);

        // Assert
        assertNotNull(result);
        assertEquals(testRequest.getAccountId(), result.getAccountId());
        assertEquals(testRequest.getType(), result.getType());
        assertEquals(testRequest.getAmount(), result.getAmount());
        assertEquals(testRequest.getCategory(), result.getCategory());
        assertEquals(testRequest.getDescription(), result.getDescription());
        
        verify(userRepository).findByUsername("testuser");
        verify(transactionRepository).logTransaction(
            eq(1L), eq("deposit"), eq(new BigDecimal("100.00")), 
            eq("Salary"), eq("Monthly salary"), any(Timestamp.class)
        );
    }

    @Test
    void testCreateTransaction_WithoutCategory() {
        // Arrange
        testRequest.setCategory(null);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(transactionRepository.logTransaction(
            eq(1L), eq("deposit"), eq(new BigDecimal("100.00")), eq("Monthly salary")
        )).thenReturn(1);

        // Act
        TransactionDTO result = transactionService.createTransaction("testuser", testRequest);

        // Assert
        assertNotNull(result);
        assertEquals(testRequest.getAccountId(), result.getAccountId());
        assertEquals(testRequest.getType(), result.getType());
        assertEquals(testRequest.getAmount(), result.getAmount());
        
        verify(userRepository).findByUsername("testuser");
        verify(transactionRepository).logTransaction(
            eq(1L), eq("deposit"), eq(new BigDecimal("100.00")), eq("Monthly salary")
        );
    }

    @Test
    void testCreateTransaction_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            transactionService.createTransaction("nonexistent", testRequest);
        });
        
        verify(userRepository).findByUsername("nonexistent");
        verify(transactionRepository, never()).logTransaction(any(), any(), any(), any(), any(), any());
    }

    @Test
    void testCreateTransaction_Failed() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(transactionRepository.logTransaction(
            eq(1L), eq("deposit"), eq(new BigDecimal("100.00")), 
            eq("Salary"), eq("Monthly salary"), any(Timestamp.class)
        )).thenReturn(0);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            transactionService.createTransaction("testuser", testRequest);
        });
        
        verify(userRepository).findByUsername("testuser");
        verify(transactionRepository).logTransaction(
            eq(1L), eq("deposit"), eq(new BigDecimal("100.00")), 
            eq("Salary"), eq("Monthly salary"), any(Timestamp.class)
        );
    }

    @Test
    void testGetSpendingByCategory_Success() {
        // Arrange
        Map<String, BigDecimal> expectedCategories = new HashMap<>();
        expectedCategories.put("Food", new BigDecimal("500.00"));
        expectedCategories.put("Transport", new BigDecimal("200.00"));
        
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(transactionRepository.getSpendingByCategory(1L, 30)).thenReturn(expectedCategories);

        // Act
        Map<String, BigDecimal> result = transactionService.getSpendingByCategory("testuser", 30);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(new BigDecimal("500.00"), result.get("Food"));
        assertEquals(new BigDecimal("200.00"), result.get("Transport"));
        
        verify(userRepository).findByUsername("testuser");
        verify(transactionRepository).getSpendingByCategory(1L, 30);
    }

    @Test
    void testGetTransactionSummary_Success() {
        // Arrange
        Map<String, BigDecimal> categories = new HashMap<>();
        categories.put("Food", new BigDecimal("500.00"));
        categories.put("Transport", new BigDecimal("200.00"));
        
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(transactionRepository.getSpendingByCategory(1L, 30)).thenReturn(categories);

        // Act
        Map<String, Object> result = transactionService.getTransactionSummary("testuser", 30);

        // Assert
        assertNotNull(result);
        assertEquals(4, result.size());
        assertEquals(categories, result.get("spendingByCategory"));
        assertEquals(new BigDecimal("700.00"), result.get("totalSpending"));
        assertEquals(30, result.get("days"));
        assertEquals(1L, result.get("userId"));
        
        verify(userRepository).findByUsername("testuser");
        verify(transactionRepository).getSpendingByCategory(1L, 30);
    }

    @Test
    void testGetTransactionsByAccount_Success() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(transactionRepository.findByAccountId(1L)).thenReturn(Arrays.asList(testTransaction));

        // Act
        List<TransactionDTO> result = transactionService.getTransactionsByAccount("testuser", 1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTransaction.getId(), result.get(0).getId());
        assertEquals(testTransaction.getAccountId(), result.get(0).getAccountId());
        
        verify(userRepository).findByUsername("testuser");
        verify(transactionRepository).findByAccountId(1L);
    }
} 