package com.jarom.funbankapp.repository;

import com.jarom.funbankapp.model.Transaction;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface TransactionRepository {
    
    // Create operations
    int logTransaction(Long accountId, String type, BigDecimal amount, String description);
    
    // Read operations
    List<Transaction> findByAccountId(Long accountId);
    List<Transaction> getRecentTransactions(Long userId, int limit);
    
    // Analytics operations
    Map<String, BigDecimal> getSpendingByCategory(Long userId, int days);
} 