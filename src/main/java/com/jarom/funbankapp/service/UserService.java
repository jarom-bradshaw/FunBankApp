package com.jarom.funbankapp.service;

import java.math.BigDecimal;
import java.util.List;

import com.jarom.funbankapp.dto.UserUpdateRequest;
import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.AccountRepository;
import com.jarom.funbankapp.repository.BudgetRepository;
import com.jarom.funbankapp.repository.GoalRepository;
import com.jarom.funbankapp.repository.TransactionRepository;
import com.jarom.funbankapp.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;
    private final GoalRepository goalRepository;

    public UserService(UserRepository userRepository, AccountRepository accountRepository, 
                      TransactionRepository transactionRepository, BudgetRepository budgetRepository, 
                      GoalRepository goalRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.budgetRepository = budgetRepository;
        this.goalRepository = goalRepository;
    }

    public Long save(User user) {
        return userRepository.save(user);
    }

    public java.util.Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public java.util.Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void updateUser(User user) {
        userRepository.updateUser(user);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Partially update a user's profile information
     * @param username the username of the user to update
     * @param request the update request containing the fields to update
     * @return the updated user
     */
    public User patchUser(String username, UserUpdateRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Update only the provided fields
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getEmail() != null) {
            // Check if email is already taken by another user
            if (!user.getEmail().equals(request.getEmail()) && 
                userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email is already taken");
            }
            user.setEmail(request.getEmail());
        }
        
        userRepository.updateUser(user);
        return user;
    }

    /**
     * Delete a user account and all associated data
     * @param username the username to delete
     */
    public void deleteUserAccount(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Get all user accounts
        List<com.jarom.funbankapp.model.Account> accounts = accountRepository.findByUserId(user.getId());
        
        // Check if any account has a positive balance
        for (com.jarom.funbankapp.model.Account account : accounts) {
            if (account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
                throw new RuntimeException("Cannot delete user account with accounts that have positive balance. Please withdraw all funds first.");
            }
        }
        
        // Delete all transactions for all user accounts
        for (com.jarom.funbankapp.model.Account account : accounts) {
            transactionRepository.deleteByAccountId(account.getId());
        }
        
        // Delete all budgets for the user
        budgetRepository.deleteByUserId(user.getId());
        
        // Delete all goals for the user
        goalRepository.deleteByUserId(user.getId());
        
        // Delete all accounts
        for (com.jarom.funbankapp.model.Account account : accounts) {
            accountRepository.deleteAccount(account.getId());
        }
        
        // Finally, delete the user
        userRepository.deleteUser(user.getId());
    }
} 