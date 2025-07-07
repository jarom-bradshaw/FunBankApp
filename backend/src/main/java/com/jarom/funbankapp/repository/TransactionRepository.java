package com.jarom.funbankapp.repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.jarom.funbankapp.model.Transaction;

public interface TransactionRepository {
    
    // Create operations
    int logTransaction(Long accountId, String type, BigDecimal amount, String description);
    int logTransaction(Long accountId, String type, BigDecimal amount, String category, String description);
    int logTransaction(Long accountId, String type, BigDecimal amount, String category, String description, Timestamp transactionDate);
    
    // Read operations
    List<Transaction> findByAccountId(Long accountId);
    List<Transaction> getRecentTransactions(Long userId, int limit);
    Optional<Transaction> findById(Long transactionId);
    
    // Update operations
    Transaction save(Transaction transaction);
    
    // Delete operations
    void deleteById(Long transactionId);
    int deleteByAccountId(Long accountId);
    
    // Authorization operations
    boolean isAccountOwnedByUser(Long accountId, Long userId);
    
    // Analytics operations
    Map<String, BigDecimal> getSpendingByCategory(Long userId, int days);
    Map<String, BigDecimal> getIncomeByCategory(Long userId, int days);
    Map<String, BigDecimal> getSpendingByMonth(Long userId, int months);
    Long getTransactionCount(Long userId, int days);
    Long getTransactionCountByType(Long userId, String type, int days);
    
    // Enhanced analytics operations
    Long getTotalTransactionsInMonths(Long userId, int months);
    BigDecimal getTotalSpendingInMonths(Long userId, int months);
    BigDecimal getAverageTransactionAmountInMonths(Long userId, int months);
    Map<String, BigDecimal> getMonthlySpendingByYear(Long userId, int year);
    Map<String, Long> getMonthlyTransactionCountByYear(Long userId, int year);
    Map<String, BigDecimal> getTopCategoriesByYear(Long userId, int year);
} 