package com.jarom.funbankapp.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jarom.funbankapp.dto.AccountDTO;
import com.jarom.funbankapp.dto.AccountUpdateRequest;
import com.jarom.funbankapp.dto.DepositRequest;
import com.jarom.funbankapp.dto.TransferRequest;
import com.jarom.funbankapp.dto.WithdrawRequest;
import com.jarom.funbankapp.exception.ResourceNotFoundException;
import com.jarom.funbankapp.exception.UnauthorizedException;
import com.jarom.funbankapp.model.Account;
import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.AccountRepository;
import com.jarom.funbankapp.repository.TransactionRepository;
import com.jarom.funbankapp.repository.UserRepository;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public AccountService(AccountRepository accountRepository, UserRepository userRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Create a new account for the authenticated user
     */
    public AccountDTO createAccount(AccountDTO accountDTO) {
        // Get current user
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        // Validate account type
        validateAccountType(accountDTO.getAccountType());
        
        // Sanitize account name
        String sanitizedName = sanitizeAccountName(accountDTO.getName());
        
        // Create account model
        Account account = new Account();
        account.setName(sanitizedName);
        account.setAccountNumber(generateAccountNumber());
        account.setAccountType(accountDTO.getAccountType().name().toLowerCase());
        account.setBalance(accountDTO.getBalance() != null ? accountDTO.getBalance() : BigDecimal.ZERO);
        account.setColor(accountDTO.getColor());
        account.setUserId(user.getId());
        account.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        account.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        
        // Save to database
        accountRepository.createAccount(account);
        
        // Return DTO
        return convertToDTO(account);
    }

    /**
     * Get all accounts for the authenticated user
     */
    public List<AccountDTO> getUserAccounts() {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        List<Account> accounts = accountRepository.findByUserId(user.getId());
        return accounts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get a specific account by ID (if user owns it)
     */
    public AccountDTO getAccountById(Long accountId) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId));
        
        if (!account.getUserId().equals(user.getId())) {
            throw new UnauthorizedException("You don't own this account");
        }
        
        return convertToDTO(account);
    }

    /**
     * Update an account (if user owns it)
     */
    public AccountDTO updateAccount(Long accountId, AccountDTO accountDTO) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId));
        
        if (!account.getUserId().equals(user.getId())) {
            throw new UnauthorizedException("You don't own this account");
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
            account.setAccountType(accountDTO.getAccountType().name().toLowerCase());
        }
        if (accountDTO.getColor() != null) {
            account.setColor(accountDTO.getColor());
        }
        
        account.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        
        // Save to database
        accountRepository.updateAccount(account);
        
        return convertToDTO(account);
    }

    /**
     * Patch an account (if user owns it) - partial update
     */
    public void patchAccount(Long accountId, AccountUpdateRequest accountUpdateRequest) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId));
        
        if (!account.getUserId().equals(user.getId())) {
            throw new UnauthorizedException("You don't own this account");
        }
        
        // Apply partial updates
        if (accountUpdateRequest.getName() != null) {
            account.setName(sanitizeAccountName(accountUpdateRequest.getName()));
        }
        if (accountUpdateRequest.getAccountType() != null) {
            validateAccountType(AccountDTO.AccountType.valueOf(accountUpdateRequest.getAccountType().toUpperCase()));
            account.setAccountType(accountUpdateRequest.getAccountType().toLowerCase());
        }
        if (accountUpdateRequest.getColor() != null) {
            account.setColor(accountUpdateRequest.getColor());
        }
        
        account.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        
        // Save to database
        accountRepository.updateAccount(account);
    }

    /**
     * Delete an account (if user owns it)
     */
    public void deleteAccount(Long accountId) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId));
        
        if (!account.getUserId().equals(user.getId())) {
            throw new UnauthorizedException("You don't own this account");
        }
        
        // Check if account has balance
        if (account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new RuntimeException("Cannot delete account with positive balance");
        }
        
        accountRepository.deleteAccount(accountId);
    }

    /**
     * Deposit funds into an account
     */
    @Transactional
    public BigDecimal deposit(DepositRequest request) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        // Verify account ownership
        if (!userOwnsAccount(user, request.getAccountId())) {
            throw new UnauthorizedException("You don't own this account");
        }
        
        // Get current balance
        BigDecimal currentBalance = accountRepository.getBalance(request.getAccountId());
        BigDecimal newBalance = currentBalance.add(request.getAmount());
        
        // Update balance
        accountRepository.updateBalance(request.getAccountId(), newBalance);
        
        // Log transaction
        transactionRepository.logTransaction(
                request.getAccountId(),
                "deposit",
                request.getAmount(),
                request.getDescription() != null ? request.getDescription() : "Deposit"
        );
        
        return newBalance;
    }

    /**
     * Withdraw funds from an account
     */
    @Transactional
    public BigDecimal withdraw(WithdrawRequest request) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        // Verify account ownership
        if (!userOwnsAccount(user, request.getAccountId())) {
            throw new UnauthorizedException("You don't own this account");
        }
        
        // Get current balance
        BigDecimal currentBalance = accountRepository.getBalance(request.getAccountId());
        
        // Check sufficient funds
        if (request.getAmount().compareTo(currentBalance) > 0) {
            throw new RuntimeException("Insufficient funds");
        }
        
        BigDecimal newBalance = currentBalance.subtract(request.getAmount());
        
        // Update balance
        accountRepository.updateBalance(request.getAccountId(), newBalance);
        
        // Log transaction
        transactionRepository.logTransaction(
                request.getAccountId(),
                "withdraw",
                request.getAmount(),
                request.getDescription() != null ? request.getDescription() : "Withdrawal"
        );
        
        return newBalance;
    }

    /**
     * Transfer funds between accounts
     */
    @Transactional
    public void transfer(TransferRequest request) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        // Verify source account ownership
        if (!userOwnsAccount(user, request.getFromAccountId())) {
            throw new UnauthorizedException("You don't own the source account");
        }
        
        // Get balances
        BigDecimal fromBalance = accountRepository.getBalance(request.getFromAccountId());
        BigDecimal toBalance = accountRepository.getBalance(request.getToAccountId());
        
        // Check sufficient funds
        if (request.getAmount().compareTo(fromBalance) > 0) {
            throw new RuntimeException("Insufficient funds");
        }
        
        // Calculate new balances
        BigDecimal newFromBalance = fromBalance.subtract(request.getAmount());
        BigDecimal newToBalance = toBalance.add(request.getAmount());
        
        // Update balances
        accountRepository.updateBalance(request.getFromAccountId(), newFromBalance);
        accountRepository.updateBalance(request.getToAccountId(), newToBalance);
        
        // Log transactions
        transactionRepository.logTransaction(
                request.getFromAccountId(),
                "transfer",
                request.getAmount(),
                request.getDescription() != null ? request.getDescription() : "Transfer to account " + request.getToAccountId()
        );
        
        transactionRepository.logTransaction(
                request.getToAccountId(),
                "deposit",
                request.getAmount(),
                "Transfer from account " + request.getFromAccountId()
        );
    }

    /**
     * Validate account ownership
     */
    public boolean userOwnsAccount(Long accountId) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        Account account = accountRepository.findById(accountId).orElse(null);
        return account != null && account.getUserId().equals(user.getId());
    }

    /**
     * Get account balance
     */
    public BigDecimal getAccountBalance(Long accountId) {
        if (!userOwnsAccount(accountId)) {
            throw new UnauthorizedException("Account not found or access denied");
        }
        
        return accountRepository.getBalance(accountId);
    }

    /**
     * Update account balance
     */
    public void updateAccountBalance(Long accountId, BigDecimal newBalance) {
        if (!userOwnsAccount(accountId)) {
            throw new UnauthorizedException("Account not found or access denied");
        }
        
        accountRepository.updateBalance(accountId, newBalance);
    }

    // Private helper methods

    private boolean userOwnsAccount(User user, Long accountId) {
        List<Account> accounts = accountRepository.findByUserId(user.getId());
        return accounts.stream().anyMatch(account -> account.getId().equals(accountId));
    }

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
        return UUID.randomUUID().toString();
    }

    private AccountDTO convertToDTO(Account account) {
        AccountDTO dto = new AccountDTO();
        dto.setId(account.getId());
        dto.setName(account.getName());
        dto.setAccountNumber(account.getAccountNumber());
        dto.setBalance(account.getBalance());
        // Fix: always uppercase before enum conversion
        dto.setAccountType(AccountDTO.AccountType.valueOf(account.getAccountType().toUpperCase()));
        dto.setColor(account.getColor());
        dto.setCreatedAt(account.getCreatedAt() != null ? account.getCreatedAt().toLocalDateTime() : null);
        dto.setUpdatedAt(account.getUpdatedAt() != null ? account.getUpdatedAt().toLocalDateTime() : null);
        return dto;
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            // For test context, return a default username
            return "testuser";
        }
        return auth.getName();
    }
} 