package com.jarom.funbankapp.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.jarom.funbankapp.dto.TransactionDTO;
import com.jarom.funbankapp.dto.TransactionRequest;
import com.jarom.funbankapp.dto.TransactionUpdateRequest;
import com.jarom.funbankapp.dto.TransferRequest;

/**
 * Service layer for transaction operations
 * Handles business logic and data transformation
 */
public interface TransactionService {
    
    /**
     * Get recent transactions for a user
     * @param username the username
     * @param limit maximum number of transactions to return
     * @return list of transaction DTOs
     */
    List<TransactionDTO> getRecentTransactions(String username, int limit);
    
    /**
     * Create a new transaction
     * @param username the username
     * @param request transaction request
     * @return created transaction DTO
     */
    TransactionDTO createTransaction(String username, TransactionRequest request);
    
    /**
     * Transfer funds between two accounts
     * @param username the username
     * @param request transfer request
     * @return map containing both debit and credit transactions
     */
    Map<String, TransactionDTO> transferBetweenAccounts(String username, TransferRequest request);
    
    /**
     * Get spending by category for a user
     * @param username the username
     * @param days number of days to look back
     * @return map of category to total spending
     */
    Map<String, BigDecimal> getSpendingByCategory(String username, int days);
    
    /**
     * Get transaction summary for a user
     * @param username the username
     * @param days number of days to look back
     * @return transaction summary data
     */
    Map<String, Object> getTransactionSummary(String username, int days);
    
    /**
     * Get transaction trends over time
     * @param username the username
     * @param months number of months to analyze
     * @return transaction trends data
     */
    Map<String, Object> getTransactionTrends(String username, int months);
    
    /**
     * Get monthly transaction analysis
     * @param username the username
     * @param year year to analyze
     * @return monthly analysis data
     */
    Map<String, Object> getMonthlyAnalysis(String username, int year);
    
    /**
     * Get transactions by account ID
     * @param username the username
     * @param accountId the account ID
     * @return list of transaction DTOs
     */
    List<TransactionDTO> getTransactionsByAccount(String username, Long accountId);

    /**
     * Update an existing transaction with complete data
     * @param username the username
     * @param transactionId the transaction ID
     * @param request update request with all fields
     * @return updated transaction DTO
     */
    TransactionDTO updateTransaction(String username, Long transactionId, TransactionUpdateRequest request);

    /**
     * Partially update an existing transaction
     * @param username the username
     * @param transactionId the transaction ID
     * @param request update request with only fields to update
     * @return updated transaction DTO
     */
    TransactionDTO patchTransaction(String username, Long transactionId, TransactionUpdateRequest request);

    /**
     * Get a specific transaction by ID
     * @param username the username
     * @param transactionId the transaction ID
     * @return transaction DTO
     */
    TransactionDTO getTransactionById(String username, Long transactionId);

    /**
     * Delete a specific transaction
     * @param username the username
     * @param transactionId the transaction ID
     */
    void deleteTransaction(String username, Long transactionId);

    /**
     * Delete all transactions for a specific account
     * @param username the username
     * @param accountId the account ID
     * @return number of transactions deleted
     */
    int deleteTransactionsByAccount(String username, Long accountId);
} 