package com.jarom.funbankapp.service;

import com.jarom.funbankapp.model.Transaction;
import com.jarom.funbankapp.repository.TransactionRepository;
import com.jarom.funbankapp.repository.UserRepository;
import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.exception.UnauthorizedException;
import com.jarom.funbankapp.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    /**
     * Get all transactions for a user's accounts
     */
    public List<Transaction> getUserTransactions() {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        return transactionRepository.getRecentTransactions(user.getId(), 100); // Default limit
    }

    /**
     * Get recent transactions for a user
     */
    public List<Transaction> getRecentTransactions(int limit) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        // Validate limit
        if (limit <= 0 || limit > 1000) {
            limit = 10; // Default limit
        }
        
        return transactionRepository.getRecentTransactions(user.getId(), limit);
    }

    /**
     * Get transactions for a specific account (if user owns it)
     */
    public List<Transaction> getAccountTransactions(Long accountId) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        // TODO: Add account ownership validation
        // This would require AccountRepository injection to verify ownership
        
        return transactionRepository.findByAccountId(accountId);
    }

    /**
     * Import transactions for a user
     */
    public int importTransactions(List<Transaction> transactions) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        // Validate and sanitize transactions
        int importedCount = 0;
        for (Transaction transaction : transactions) {
            // Validate transaction data
            if (isValidTransaction(transaction)) {
                transaction.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                
                // TODO: Add account ownership validation
                int result = transactionRepository.logTransaction(
                    transaction.getAccountId(),
                    transaction.getType(),
                    transaction.getAmount(),
                    transaction.getDescription()
                );
                if (result > 0) {
                    importedCount++;
                }
            }
        }
        
        return importedCount;
    }

    /**
     * Get spending analysis by category
     */
    public Map<String, BigDecimal> getSpendingByCategory(int days) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        // Validate days parameter
        if (days <= 0 || days > 365) {
            days = 30; // Default to 30 days
        }
        
        return transactionRepository.getSpendingByCategory(user.getId(), days);
    }

    /**
     * Get transaction categories
     */
    public Map<String, BigDecimal> getTransactionCategories(int days) {
        return getSpendingByCategory(days);
    }

    // Private helper methods

    private boolean isValidTransaction(Transaction transaction) {
        return transaction != null &&
               transaction.getAccountId() != null &&
               transaction.getType() != null &&
               transaction.getAmount() != null &&
               transaction.getAmount().compareTo(BigDecimal.ZERO) > 0 &&
               isValidTransactionType(transaction.getType());
    }

    private boolean isValidTransactionType(String type) {
        return "deposit".equals(type) || 
               "withdraw".equals(type) || 
               "transfer".equals(type);
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
} 