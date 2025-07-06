package com.jarom.funbankapp.controller;

import com.jarom.funbankapp.dto.ApiResponse;
import com.jarom.funbankapp.model.Goal;
import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.GoalRepository;
import com.jarom.funbankapp.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    public GoalController(GoalRepository goalRepository, UserRepository userRepository) {
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Goal>> createGoal(@RequestBody Goal request, Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            request.setUserId(user.getId());
            goalRepository.createGoal(request);

            return ResponseEntity.ok(ApiResponse.success("Goal created successfully", request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to create goal: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Goal>>> getGoals(Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Goal> goals = goalRepository.findByUserId(user.getId());
            return ResponseEntity.ok(ApiResponse.success("Goals retrieved successfully", goals));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve goals: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Goal>> getGoal(@PathVariable Long id, Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Goal goal = goalRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Goal not found"));

            if (!goal.getUserId().equals(user.getId())) {
                return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
            }

            return ResponseEntity.ok(ApiResponse.success("Goal retrieved successfully", goal));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve goal: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Goal>> updateGoal(@PathVariable Long id, @RequestBody Goal request, Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Goal existingGoal = goalRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Goal not found"));

            if (!existingGoal.getUserId().equals(user.getId())) {
                return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
            }

            request.setId(id);
            request.setUserId(user.getId());
            goalRepository.updateGoal(request);

            return ResponseEntity.ok(ApiResponse.success("Goal updated successfully", request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to update goal: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteGoal(@PathVariable Long id, Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Goal goal = goalRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Goal not found"));

            if (!goal.getUserId().equals(user.getId())) {
                return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
            }

            goalRepository.deleteGoal(id);
            return ResponseEntity.ok(ApiResponse.success("Goal deleted successfully", "Goal deleted"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to delete goal: " + e.getMessage()));
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<List<Goal>>> getGoalSummary(Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Goal> goals = goalRepository.findByUserId(user.getId());
            return ResponseEntity.ok(ApiResponse.success("Goal summary retrieved successfully", goals));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve goal summary: " + e.getMessage()));
        }
    }
} 