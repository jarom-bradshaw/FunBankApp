package com.jarom.funbankapp.repository;

import com.jarom.funbankapp.model.Budget;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BudgetRepository {
    
    // Create operations
    void createBudget(Budget budget);
    
    // Read operations
    List<Budget> findByUserId(Long userId);
    Optional<Budget> findById(Long budgetId);
    
    // Update operations
    void updateBudget(Budget budget);
    void updateSpent(Long budgetId, BigDecimal spent);
    
    // Delete operations
    void deleteBudget(Long budgetId);
} 