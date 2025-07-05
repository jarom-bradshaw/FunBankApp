package com.jarom.funbankapp.service;

import com.jarom.funbankapp.dto.AccountDTO;
import com.jarom.funbankapp.model.Account;
import com.jarom.funbankapp.repository.AccountDAO;
import com.jarom.funbankapp.repository.UserDAO;
import com.jarom.funbankapp.model.User;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private final AccountDAO accountDAO;
    private final UserDAO userDAO;

    public AccountService(AccountDAO accountDAO, UserDAO userDAO) {
        this.accountDAO = accountDAO;
        this.userDAO = userDAO;
    }

    /**
     * Create a new account for the authenticated user
     */
    public AccountDTO createAccount(AccountDTO accountDTO) {
        // Get current user
        String username = getCurrentUsername();
        User user = userDAO.findByUsername(username);
        
        // Validate account type
        validateAccountType(accountDTO.getAccountType());
        
        // Sanitize account name
        String sanitizedName = sanitizeAccountName(accountDTO.getName());
        
        // Create account model
        Account account = new Account();
        account.setName(sanitizedName);
        account.setAccountNumber(generateAccountNumber());
        account.setAccountType(accountDTO.getAccountType().name());
        account.setBalance(accountDTO.getBalance() != null ? accountDTO.getBalance() : BigDecimal.ZERO);
        account.setColor(accountDTO.getColor());
        account.setUserId(user.getId());
        account.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        account.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        
        // Save to database
        accountDAO.createAccount(account);
        
        // Return DTO
        return convertToDTO(account);
    }

    /**
     * Get all accounts for the authenticated user
     */
    public List<AccountDTO> getUserAccounts() {
        String username = getCurrentUsername();
        User user = userDAO.findByUsername(username);
        
        List<Account> accounts = accountDAO.findByUserId(user.getId());
        return accounts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get a specific account by ID (if user owns it)
     */
    public AccountDTO getAccountById(Long accountId) {
        String username = getCurrentUsername();
        User user = userDAO.findByUsername(username);
        
        Account account = accountDAO.findById(accountId);
        if (account == null || !account.getUserId().equals(user.getId())) {
            throw new RuntimeException("Account not found or access denied");
        }
        
        return convertToDTO(account);
    }

    /**
     * Update an account (if user owns it)
     */
    public AccountDTO updateAccount(Long accountId, AccountDTO accountDTO) {
        String username = getCurrentUsername();
        User user = userDAO.findByUsername(username);
        
        Account account = accountDAO.findById(accountId);
        if (account == null || !account.getUserId().equals(user.getId())) {
            throw new RuntimeException("Account not found or access denied");
        }
        
        // Validate account type
        if (accountDTO.getAccountType() != null) {
            validateAccountType(accountDTO.getAccountType());
        }
        
        // Update fields
        if (accountDTO.getName() != null) {
            account.setName(sanitizeAccountName(accountDTO.getName()));
        }
        if (accountDTO.getAccountType() != null) {
            account.setAccountType(accountDTO.getAccountType().name());
        }
        if (accountDTO.getColor() != null) {
            account.setColor(accountDTO.getColor());
        }
        
        account.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        
        // Save to database
        accountDAO.updateAccount(account);
        
        return convertToDTO(account);
    }

    /**
     * Delete an account (if user owns it)
     */
    public void deleteAccount(Long accountId) {
        String username = getCurrentUsername();
        User user = userDAO.findByUsername(username);
        
        Account account = accountDAO.findById(accountId);
        if (account == null || !account.getUserId().equals(user.getId())) {
            throw new RuntimeException("Account not found or access denied");
        }
        
        // Check if account has balance
        if (account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new RuntimeException("Cannot delete account with positive balance");
        }
        
        accountDAO.deleteAccount(accountId);
    }

    /**
     * Validate account ownership
     */
    public boolean userOwnsAccount(Long accountId) {
        String username = getCurrentUsername();
        User user = userDAO.findByUsername(username);
        
        Account account = accountDAO.findById(accountId);
        return account != null && account.getUserId().equals(user.getId());
    }

    /**
     * Get account balance
     */
    public BigDecimal getAccountBalance(Long accountId) {
        if (!userOwnsAccount(accountId)) {
            throw new RuntimeException("Account not found or access denied");
        }
        
        return accountDAO.getBalance(accountId);
    }

    /**
     * Update account balance
     */
    public void updateAccountBalance(Long accountId, BigDecimal newBalance) {
        if (!userOwnsAccount(accountId)) {
            throw new RuntimeException("Account not found or access denied");
        }
        
        accountDAO.updateBalance(accountId, newBalance);
    }

    // Private helper methods

    private void validateAccountType(AccountDTO.AccountType accountType) {
        if (accountType == null) {
            throw new RuntimeException("Account type is required");
        }
        
        try {
            AccountDTO.AccountType.valueOf(accountType.name());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid account type: " + accountType);
        }
    }

    private String sanitizeAccountName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new RuntimeException("Account name is required");
        }
        
        String sanitized = name.trim();
        if (sanitized.length() > 255) {
            sanitized = sanitized.substring(0, 255);
        }
        
        return sanitized;
    }

    private String generateAccountNumber() {
        return java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    private AccountDTO convertToDTO(Account account) {
        AccountDTO dto = new AccountDTO();
        dto.setId(account.getId());
        dto.setName(account.getName());
        dto.setAccountNumber(account.getAccountNumber());
        dto.setAccountType(AccountDTO.AccountType.valueOf(account.getAccountType()));
        dto.setBalance(account.getBalance());
        dto.setColor(account.getColor());
        dto.setUserId(account.getUserId());
        dto.setCreatedAt(account.getCreatedAt() != null ? account.getCreatedAt().toLocalDateTime() : null);
        dto.setUpdatedAt(account.getUpdatedAt() != null ? account.getUpdatedAt().toLocalDateTime() : null);
        return dto;
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }
} 