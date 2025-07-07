package com.jarom.funbankapp.service;

import com.jarom.funbankapp.model.Goal;
import com.jarom.funbankapp.repository.GoalRepository;
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
public class GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    public GoalService(GoalRepository goalRepository, UserRepository userRepository) {
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
    }

    /**
     * Create a new goal for the authenticated user
     */
    public Goal createGoal(Goal goal) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        // Validate goal data
        validateGoal(goal);
        
        // Set user and timestamps
        goal.setUserId(user.getId());
        goal.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        goal.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        
        // Sanitize data
        goal.setName(sanitizeGoalName(goal.getName()));
        if (goal.getDescription() != null) {
            goal.setDescription(sanitizeDescription(goal.getDescription()));
        }
        
        goalRepository.createGoal(goal);
        return goal;
    }

    /**
     * Get all goals for the authenticated user
     */
    public List<Goal> getUserGoals() {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        return goalRepository.findByUserId(user.getId());
    }

    /**
     * Get a specific goal by ID (if user owns it)
     */
    public Goal getGoalById(Long goalId) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal", "id", goalId));
        
        if (!goal.getUserId().equals(user.getId())) {
            throw new UnauthorizedException("You don't own this goal");
        }
        
        return goal;
    }

    /**
     * Update a goal (if user owns it)
     */
    public Goal updateGoal(Long goalId, Goal goalUpdate) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        Goal existingGoal = goalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal", "id", goalId));
        
        if (!existingGoal.getUserId().equals(user.getId())) {
            throw new UnauthorizedException("You don't own this goal");
        }
        
        // Update fields if provided
        if (goalUpdate.getName() != null) {
            existingGoal.setName(sanitizeGoalName(goalUpdate.getName()));
        }
        if (goalUpdate.getDescription() != null) {
            existingGoal.setDescription(sanitizeDescription(goalUpdate.getDescription()));
        }
        if (goalUpdate.getTargetAmount() != null) {
            validateAmount(goalUpdate.getTargetAmount());
            existingGoal.setTargetAmount(goalUpdate.getTargetAmount());
        }
        if (goalUpdate.getCurrentAmount() != null) {
            validateAmount(goalUpdate.getCurrentAmount());
            existingGoal.setCurrentAmount(goalUpdate.getCurrentAmount());
        }
        if (goalUpdate.getDeadline() != null) {
            existingGoal.setDeadline(goalUpdate.getDeadline());
        }
        
        existingGoal.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        
        goalRepository.updateGoal(existingGoal);
        return existingGoal;
    }

    /**
     * Delete a goal (if user owns it)
     */
    public void deleteGoal(Long goalId) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal", "id", goalId));
        
        if (!goal.getUserId().equals(user.getId())) {
            throw new UnauthorizedException("You don't own this goal");
        }
        
        goalRepository.deleteGoal(goalId);
    }

    /**
     * Update goal progress
     */
    public Goal updateGoalProgress(Long goalId, BigDecimal amount) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal", "id", goalId));
        
        if (!goal.getUserId().equals(user.getId())) {
            throw new UnauthorizedException("You don't own this goal");
        }
        
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount must be non-negative");
        }
        
        goal.setCurrentAmount(goal.getCurrentAmount().add(amount));
        goal.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        
        goalRepository.updateGoal(goal);
        return goal;
    }

    /**
     * Get goal progress summary for the authenticated user
     */
    public GoalProgress getGoalProgress() {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        List<Goal> goals = goalRepository.findByUserId(user.getId());
        
        BigDecimal totalTarget = goals.stream()
                .map(Goal::getTargetAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalCurrent = goals.stream()
                .map(Goal::getCurrentAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal overallProgress = totalTarget.compareTo(BigDecimal.ZERO) > 0 
            ? totalCurrent.divide(totalTarget, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"))
            : BigDecimal.ZERO;
        
        return new GoalProgress(goals, totalTarget, totalCurrent, overallProgress);
    }

    // Private helper methods

    private void validateGoal(Goal goal) {
        if (goal.getName() == null || goal.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Goal name is required");
        }
        
        validateAmount(goal.getTargetAmount());
        
        if (goal.getCurrentAmount() == null) {
            goal.setCurrentAmount(BigDecimal.ZERO);
        }
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Target amount must be greater than zero");
        }
        
        if (amount.compareTo(new BigDecimal("999999999.99")) > 0) {
            throw new IllegalArgumentException("Target amount is too large");
        }
    }

    private String sanitizeGoalName(String name) {
        if (name == null) return null;
        
        String sanitized = name.trim();
        if (sanitized.length() > 100) {
            sanitized = sanitized.substring(0, 100);
        }
        return sanitized;
    }

    private String sanitizeDescription(String description) {
        if (description == null) return null;
        
        String sanitized = description.trim();
        if (sanitized.length() > 500) {
            sanitized = sanitized.substring(0, 500);
        }
        return sanitized;
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    // Inner class for goal progress
    public static class GoalProgress {
        private final List<Goal> goals;
        private final BigDecimal totalTarget;
        private final BigDecimal totalCurrent;
        private final BigDecimal overallProgress;

        public GoalProgress(List<Goal> goals, BigDecimal totalTarget, BigDecimal totalCurrent, BigDecimal overallProgress) {
            this.goals = goals;
            this.totalTarget = totalTarget;
            this.totalCurrent = totalCurrent;
            this.overallProgress = overallProgress;
        }

        // Getters
        public List<Goal> getGoals() { return goals; }
        public BigDecimal getTotalTarget() { return totalTarget; }
        public BigDecimal getTotalCurrent() { return totalCurrent; }
        public BigDecimal getOverallProgress() { return overallProgress; }
    }
} 