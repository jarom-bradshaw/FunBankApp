package com.jarom.funbankapp.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jarom.funbankapp.dto.TransactionDTO;
import com.jarom.funbankapp.dto.TransactionRequest;
import com.jarom.funbankapp.dto.TransactionUpdateRequest;
import com.jarom.funbankapp.dto.TransferRequest;
import com.jarom.funbankapp.model.Transaction;
import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.TransactionRepository;
import com.jarom.funbankapp.repository.UserRepository;

/**
 * Implementation of TransactionService
 * Handles business logic for transaction operations
 */
@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<TransactionDTO> getRecentTransactions(String username, int limit) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Transaction> transactions = transactionRepository.getRecentTransactions(user.getId(), limit);
        return transactions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TransactionDTO createTransaction(String username, TransactionRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Set transaction date if not provided
        Timestamp transactionDate = request.getTransactionDate();
        if (transactionDate == null) {
            transactionDate = new Timestamp(System.currentTimeMillis());
        }
        
        int result;
        if (request.getCategory() != null && !request.getCategory().trim().isEmpty()) {
            result = transactionRepository.logTransaction(
                request.getAccountId(), 
                request.getType(), 
                request.getAmount(), 
                request.getCategory(), 
                request.getDescription(),
                transactionDate
            );
        } else {
            result = transactionRepository.logTransaction(
                request.getAccountId(), 
                request.getType(), 
                request.getAmount(), 
                request.getDescription()
            );
        }
        
        if (result > 0) {
            // Create and return the transaction DTO
            TransactionDTO transactionDTO = new TransactionDTO();
            transactionDTO.setAccountId(request.getAccountId());
            transactionDTO.setType(request.getType());
            transactionDTO.setAmount(request.getAmount());
            transactionDTO.setCategory(request.getCategory());
            transactionDTO.setDescription(request.getDescription());
            transactionDTO.setTransactionDate(transactionDate);
            transactionDTO.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            
            return transactionDTO;
        } else {
            throw new RuntimeException("Failed to create transaction");
        }
    }

    @Override
    @Transactional
    public Map<String, TransactionDTO> transferBetweenAccounts(String username, TransferRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Verify both accounts belong to the user
        if (!transactionRepository.isAccountOwnedByUser(request.getSourceAccountId(), user.getId())) {
            throw new RuntimeException("Unauthorized: You don't own the source account");
        }
        if (!transactionRepository.isAccountOwnedByUser(request.getDestinationAccountId(), user.getId())) {
            throw new RuntimeException("Unauthorized: You don't own the destination account");
        }
        
        // Verify accounts are different
        if (request.getSourceAccountId().equals(request.getDestinationAccountId())) {
            throw new RuntimeException("Source and destination accounts must be different");
        }
        
        // Verify amount is positive
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Transfer amount must be greater than zero");
        }
        
        Timestamp transferDate = new Timestamp(System.currentTimeMillis());
        String description = request.getDescription() != null ? request.getDescription() : "Transfer";
        
        // Create withdrawal transaction from source account
        TransactionRequest withdrawalRequest = new TransactionRequest();
        withdrawalRequest.setAccountId(request.getSourceAccountId());
        withdrawalRequest.setType("withdraw");
        withdrawalRequest.setAmount(request.getAmount());
        withdrawalRequest.setDescription("Transfer to account: " + description);
        withdrawalRequest.setCategory(request.getCategory() != null ? request.getCategory() : "Transfer");
        withdrawalRequest.setTransactionDate(transferDate);
        
        TransactionDTO withdrawalTransaction = createTransaction(username, withdrawalRequest);
        
        // Create deposit transaction to destination account
        TransactionRequest depositRequest = new TransactionRequest();
        depositRequest.setAccountId(request.getDestinationAccountId());
        depositRequest.setType("deposit");
        depositRequest.setAmount(request.getAmount());
        depositRequest.setDescription("Transfer from account: " + description);
        depositRequest.setCategory(request.getCategory() != null ? request.getCategory() : "Transfer");
        depositRequest.setTransactionDate(transferDate);
        
        TransactionDTO depositTransaction = createTransaction(username, depositRequest);
        
        // Return both transactions
        Map<String, TransactionDTO> result = new HashMap<>();
        result.put("withdrawal", withdrawalTransaction);
        result.put("deposit", depositTransaction);
        
        return result;
    }

    @Override
    public Map<String, BigDecimal> getSpendingByCategory(String username, int days) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return transactionRepository.getSpendingByCategory(user.getId(), days);
    }

    @Override
    public Map<String, Object> getTransactionSummary(String username, int days) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Map<String, BigDecimal> categories = transactionRepository.getSpendingByCategory(user.getId(), days);
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("spendingByCategory", categories);
        summary.put("totalSpending", categories.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add));
        summary.put("days", days);
        summary.put("userId", user.getId());
        
        return summary;
    }

    @Override
    public Map<String, Object> getTransactionTrends(String username, int months) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Map<String, Object> trends = new HashMap<>();
        trends.put("userId", user.getId());
        trends.put("months", months);
        trends.put("totalTransactions", transactionRepository.getTotalTransactionsInMonths(user.getId(), months));
        trends.put("totalSpending", transactionRepository.getTotalSpendingInMonths(user.getId(), months));
        trends.put("averageTransactionAmount", transactionRepository.getAverageTransactionAmountInMonths(user.getId(), months));
        
        return trends;
    }

    @Override
    public Map<String, Object> getMonthlyAnalysis(String username, int year) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Map<String, Object> monthly = new HashMap<>();
        monthly.put("userId", user.getId());
        monthly.put("year", year);
        monthly.put("monthlySpending", transactionRepository.getMonthlySpendingByYear(user.getId(), year));
        monthly.put("monthlyTransactions", transactionRepository.getMonthlyTransactionCountByYear(user.getId(), year));
        monthly.put("topCategories", transactionRepository.getTopCategoriesByYear(user.getId(), year));
        
        return monthly;
    }

    @Override
    public List<TransactionDTO> getTransactionsByAccount(String username, Long accountId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Transaction> transactions = transactionRepository.findByAccountId(accountId);
        return transactions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TransactionDTO updateTransaction(String username, Long transactionId, TransactionUpdateRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Get the existing transaction
        Transaction existingTransaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        
        // Verify user owns the account
        if (!transactionRepository.isAccountOwnedByUser(existingTransaction.getAccountId(), user.getId())) {
            throw new RuntimeException("Unauthorized: You don't own this transaction");
        }
        
        // Update all fields
        existingTransaction.setAmount(request.getAmount());
        existingTransaction.setDescription(request.getDescription());
        existingTransaction.setCategory(request.getCategory());
        existingTransaction.setType(request.getType());
        if (request.getTransactionDate() != null) {
            existingTransaction.setTransactionDate(Timestamp.valueOf(request.getTransactionDate()));
        }
        existingTransaction.setAccountId(request.getAccountId());
        
        // Save the updated transaction
        Transaction updatedTransaction = transactionRepository.save(existingTransaction);
        return convertToDTO(updatedTransaction);
    }

    @Override
    @Transactional
    public TransactionDTO patchTransaction(String username, Long transactionId, TransactionUpdateRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Get the existing transaction
        Transaction existingTransaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        
        // Verify user owns the account
        if (!transactionRepository.isAccountOwnedByUser(existingTransaction.getAccountId(), user.getId())) {
            throw new RuntimeException("Unauthorized: You don't own this transaction");
        }
        
        // Update only provided fields (partial update)
        if (request.getAmount() != null) {
            existingTransaction.setAmount(request.getAmount());
        }
        if (request.getDescription() != null) {
            existingTransaction.setDescription(request.getDescription());
        }
        if (request.getCategory() != null) {
            existingTransaction.setCategory(request.getCategory());
        }
        if (request.getType() != null) {
            existingTransaction.setType(request.getType());
        }
        if (request.getTransactionDate() != null) {
            existingTransaction.setTransactionDate(Timestamp.valueOf(request.getTransactionDate()));
        }
        if (request.getAccountId() != null) {
            existingTransaction.setAccountId(request.getAccountId());
        }
        
        // Save the updated transaction
        Transaction updatedTransaction = transactionRepository.save(existingTransaction);
        return convertToDTO(updatedTransaction);
    }

    @Override
    public TransactionDTO getTransactionById(String username, Long transactionId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        
        // Verify user owns the account
        if (!transactionRepository.isAccountOwnedByUser(transaction.getAccountId(), user.getId())) {
            throw new RuntimeException("Unauthorized: You don't own this transaction");
        }
        
        return convertToDTO(transaction);
    }

    @Override
    @Transactional
    public void deleteTransaction(String username, Long transactionId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        
        // Verify user owns the account
        if (!transactionRepository.isAccountOwnedByUser(transaction.getAccountId(), user.getId())) {
            throw new RuntimeException("Unauthorized: You don't own this transaction");
        }
        
        // Delete the transaction
        transactionRepository.deleteById(transactionId);
    }

    @Override
    @Transactional
    public int deleteTransactionsByAccount(String username, Long accountId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Verify user owns the account
        if (!transactionRepository.isAccountOwnedByUser(accountId, user.getId())) {
            throw new RuntimeException("Unauthorized: You don't own this account");
        }
        
        // Delete all transactions for the account
        return transactionRepository.deleteByAccountId(accountId);
    }

    /**
     * Convert Transaction model to TransactionDTO
     * @param transaction the transaction model
     * @return transaction DTO
     */
    private TransactionDTO convertToDTO(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setAccountId(transaction.getAccountId());
        dto.setType(transaction.getType());
        dto.setAmount(transaction.getAmount());
        dto.setCategory(transaction.getCategory());
        dto.setDescription(transaction.getDescription());
        dto.setTransactionDate(transaction.getTransactionDate());
        dto.setCreatedAt(transaction.getCreatedAt());
        return dto;
    }
} 