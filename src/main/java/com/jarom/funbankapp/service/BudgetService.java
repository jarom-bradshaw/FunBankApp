package com.jarom.funbankapp.service;

import com.jarom.funbankapp.model.Budget;
import com.jarom.funbankapp.repository.BudgetRepository;
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

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;

    public BudgetService(BudgetRepository budgetRepository, UserRepository userRepository) {
        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
    }

    /**
     * Create a new budget for the authenticated user
     */
    public Budget createBudget(Budget budget) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        // Validate budget data
        validateBudget(budget);
        
        // Set user and timestamps
        budget.setUserId(user.getId());
        budget.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        budget.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        
        // Sanitize data
        budget.setName(sanitizeBudgetName(budget.getName()));
        budget.setCategory(sanitizeCategory(budget.getCategory()));
        
        budgetRepository.createBudget(budget);
        return budget;
    }

    /**
     * Get all budgets for the authenticated user
     */
    public List<Budget> getUserBudgets() {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        return budgetRepository.findByUserId(user.getId());
    }

    /**
     * Get a specific budget by ID (if user owns it)
     */
    public Budget getBudgetById(Long budgetId) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget", "id", budgetId));
        
        if (!budget.getUserId().equals(user.getId())) {
            throw new UnauthorizedException("You don't own this budget");
        }
        
        return budget;
    }

    /**
     * Update a budget (if user owns it)
     */
    public Budget updateBudget(Long budgetId, Budget budgetUpdate) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        Budget existingBudget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget", "id", budgetId));
        
        if (!existingBudget.getUserId().equals(user.getId())) {
            throw new UnauthorizedException("You don't own this budget");
        }
        
        // Update fields if provided
        if (budgetUpdate.getName() != null) {
            existingBudget.setName(sanitizeBudgetName(budgetUpdate.getName()));
        }
        if (budgetUpdate.getCategory() != null) {
            existingBudget.setCategory(sanitizeCategory(budgetUpdate.getCategory()));
        }
        if (budgetUpdate.getAmount() != null) {
            validateAmount(budgetUpdate.getAmount());
            existingBudget.setAmount(budgetUpdate.getAmount());
        }
        
        existingBudget.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        
        budgetRepository.updateBudget(existingBudget);
        return existingBudget;
    }

    /**
     * Delete a budget (if user owns it)
     */
    public void deleteBudget(Long budgetId) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget", "id", budgetId));
        
        if (!budget.getUserId().equals(user.getId())) {
            throw new UnauthorizedException("You don't own this budget");
        }
        
        budgetRepository.deleteBudget(budgetId);
    }

    /**
     * Get budget summary for the authenticated user
     */
    public BudgetSummary getBudgetSummary() {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        List<Budget> budgets = budgetRepository.findByUserId(user.getId());
        
        BigDecimal totalBudget = budgets.stream()
                .map(Budget::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalSpent = budgets.stream()
                .map(Budget::getSpent)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal remaining = totalBudget.subtract(totalSpent);
        
        return new BudgetSummary(budgets, totalBudget, totalSpent, remaining);
    }

    // Private helper methods

    private void validateBudget(Budget budget) {
        if (budget.getName() == null || budget.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Budget name is required");
        }
        
        if (budget.getCategory() == null || budget.getCategory().trim().isEmpty()) {
            throw new IllegalArgumentException("Budget category is required");
        }
        
        validateAmount(budget.getAmount());
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Budget amount must be greater than zero");
        }
        
        if (amount.compareTo(new BigDecimal("999999999.99")) > 0) {
            throw new IllegalArgumentException("Budget amount is too large");
        }
    }

    private String sanitizeBudgetName(String name) {
        if (name == null) return null;
        
        String sanitized = name.trim();
        if (sanitized.length() > 100) {
            sanitized = sanitized.substring(0, 100);
        }
        return sanitized;
    }

    private String sanitizeCategory(String category) {
        if (category == null) return null;
        
        String sanitized = category.trim();
        if (sanitized.length() > 50) {
            sanitized = sanitized.substring(0, 50);
        }
        return sanitized;
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    // Inner class for budget summary
    public static class BudgetSummary {
        private final List<Budget> budgets;
        private final BigDecimal totalBudget;
        private final BigDecimal totalSpent;
        private final BigDecimal remaining;

        public BudgetSummary(List<Budget> budgets, BigDecimal totalBudget, BigDecimal totalSpent, BigDecimal remaining) {
            this.budgets = budgets;
            this.totalBudget = totalBudget;
            this.totalSpent = totalSpent;
            this.remaining = remaining;
        }

        // Getters
        public List<Budget> getBudgets() { return budgets; }
        public BigDecimal getTotalBudget() { return totalBudget; }
        public BigDecimal getTotalSpent() { return totalSpent; }
        public BigDecimal getRemaining() { return remaining; }
    }
} 