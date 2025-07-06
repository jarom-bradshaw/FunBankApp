package com.jarom.funbankapp.repository;

import com.jarom.funbankapp.model.Goal;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface GoalRepository {
    
    // Create operations
    void createGoal(Goal goal);
    
    // Read operations
    List<Goal> findByUserId(Long userId);
    Optional<Goal> findById(Long goalId);
    
    // Update operations
    void updateGoal(Goal goal);
    void updateCurrentAmount(Long goalId, BigDecimal currentAmount);
    
    // Delete operations
    void deleteGoal(Long goalId);
} 