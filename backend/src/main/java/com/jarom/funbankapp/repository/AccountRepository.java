package com.jarom.funbankapp.repository;

import com.jarom.funbankapp.model.Account;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountRepository {
    
    // Create operations
    Account createAccount(Account account);
    
    // Read operations
    List<Account> findByUserId(Long userId);
    Optional<Account> findById(Long accountId);
    BigDecimal getBalance(Long accountId);
    
    // Update operations
    void updateBalance(Long accountId, BigDecimal newBalance);
    void updateAccount(Account account);
    
    // Delete operations
    void deleteAccount(Long accountId);
    
    // Legacy methods for backward compatibility
    List<Account> findByUser(com.jarom.funbankapp.model.User user);
    List<Account> findByUserAndType(com.jarom.funbankapp.model.User user, String type);
    Double getTotalBalanceByUser(com.jarom.funbankapp.model.User user);
} 