package com.jarom.funbankapp.controller;

import com.jarom.funbankapp.model.Goal;
import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.GoalDAO;
import com.jarom.funbankapp.repository.UserDAO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/goals")
@io.swagger.v3.oas.annotations.tags.Tag(name = "Goals", description = "Goal management operations")
public class GoalController {

    private final GoalDAO goalDAO;
    private final UserDAO userDAO;

    public GoalController(GoalDAO goalDAO, UserDAO userDAO) {
        this.goalDAO = goalDAO;
        this.userDAO = userDAO;
    }

    @PostMapping
    @Operation(summary = "Create a new goal", description = "Creates a new financial goal for the authenticated user.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Goal created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid goal data")
    })
    public ResponseEntity<?> createGoal(@RequestBody Goal request) {
        String username = getCurrentUsername();
        User user = userDAO.findByUsername(username);

        request.setUserId(user.getId());
        request.setCurrentAmount(BigDecimal.ZERO);
        request.setStatus("active");
        request.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        request.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        goalDAO.createGoal(request);
        return ResponseEntity.ok("Goal created successfully!");
    }

    @GetMapping
    @Operation(summary = "Get user goals", description = "Retrieves all goals for the authenticated user.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Goals retrieved successfully")
    })
    public ResponseEntity<List<Goal>> getUserGoals() {
        String username = getCurrentUsername();
        User user = userDAO.findByUsername(username);

        List<Goal> goals = goalDAO.findByUserId(user.getId());
        return ResponseEntity.ok(goals);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get goal by ID", description = "Retrieves a specific goal by ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Goal retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Goal not found"),
        @ApiResponse(responseCode = "403", description = "Unauthorized: You don't own this goal")
    })
    public ResponseEntity<?> getGoal(@PathVariable int id) {
        String username = getCurrentUsername();
        User user = userDAO.findByUsername(username);

        Goal goal = goalDAO.findById(id);
        if (goal == null) {
            return ResponseEntity.notFound().build();
        }

        if (goal.getUserId() != user.getId()) {
            return ResponseEntity.status(403).body("Unauthorized: You don't own this goal.");
        }

        return ResponseEntity.ok(goal);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update goal", description = "Updates an existing goal.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Goal updated successfully"),
        @ApiResponse(responseCode = "404", description = "Goal not found"),
        @ApiResponse(responseCode = "403", description = "Unauthorized: You don't own this goal")
    })
    public ResponseEntity<?> updateGoal(@PathVariable int id, @RequestBody Goal request) {
        String username = getCurrentUsername();
        User user = userDAO.findByUsername(username);

        Goal existingGoal = goalDAO.findById(id);
        if (existingGoal == null) {
            return ResponseEntity.notFound().build();
        }

        if (existingGoal.getUserId() != user.getId()) {
            return ResponseEntity.status(403).body("Unauthorized: You don't own this goal.");
        }

        request.setId(id);
        request.setUserId(user.getId());
        request.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        goalDAO.updateGoal(request);
        return ResponseEntity.ok("Goal updated successfully!");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete goal", description = "Deletes a goal.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Goal deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Goal not found"),
        @ApiResponse(responseCode = "403", description = "Unauthorized: You don't own this goal")
    })
    public ResponseEntity<?> deleteGoal(@PathVariable int id) {
        String username = getCurrentUsername();
        User user = userDAO.findByUsername(username);

        Goal goal = goalDAO.findById(id);
        if (goal == null) {
            return ResponseEntity.notFound().build();
        }

        if (goal.getUserId() != user.getId()) {
            return ResponseEntity.status(403).body("Unauthorized: You don't own this goal.");
        }

        goalDAO.deleteGoal(id);
        return ResponseEntity.ok("Goal deleted successfully!");
    }

    @GetMapping("/progress")
    @Operation(summary = "Get goal progress", description = "Retrieves progress summary for all user goals.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Goal progress retrieved successfully")
    })
    public ResponseEntity<?> getGoalProgress() {
        String username = getCurrentUsername();
        User user = userDAO.findByUsername(username);

        List<Goal> goals = goalDAO.findByUserId(user.getId());
        
        BigDecimal totalTarget = goals.stream()
                .map(Goal::getTargetAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalCurrent = goals.stream()
                .map(Goal::getCurrentAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal overallProgress = totalTarget.compareTo(BigDecimal.ZERO) > 0 
            ? totalCurrent.divide(totalTarget, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"))
            : BigDecimal.ZERO;

        return ResponseEntity.ok(new GoalProgress(goals, totalTarget, totalCurrent, overallProgress));
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    // Inner class for goal progress response
    public static class GoalProgress {
        private List<Goal> goals;
        private BigDecimal totalTarget;
        private BigDecimal totalCurrent;
        private BigDecimal overallProgress;

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