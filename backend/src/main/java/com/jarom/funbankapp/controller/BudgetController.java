package com.jarom.funbankapp.controller;

import com.jarom.funbankapp.dto.ApiResponse;
import com.jarom.funbankapp.model.Budget;
import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.BudgetRepository;
import com.jarom.funbankapp.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;

    public BudgetController(BudgetRepository budgetRepository, UserRepository userRepository) {
        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Budget>> createBudget(@RequestBody Budget request, Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            request.setUserId(user.getId());
            budgetRepository.createBudget(request);

            return ResponseEntity.ok(ApiResponse.success("Budget created successfully", request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to create budget: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Budget>>> getBudgets(Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Budget> budgets = budgetRepository.findByUserId(user.getId());
            return ResponseEntity.ok(ApiResponse.success("Budgets retrieved successfully", budgets));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve budgets: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Budget>> getBudget(@PathVariable Long id, Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Budget budget = budgetRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Budget not found"));

            if (!budget.getUserId().equals(user.getId())) {
                return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
            }

            return ResponseEntity.ok(ApiResponse.success("Budget retrieved successfully", budget));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve budget: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Budget>> updateBudget(@PathVariable Long id, @RequestBody Budget request, Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Budget existingBudget = budgetRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Budget not found"));

            if (!existingBudget.getUserId().equals(user.getId())) {
                return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
            }

            request.setId(id);
            request.setUserId(user.getId());
            budgetRepository.updateBudget(request);

            return ResponseEntity.ok(ApiResponse.success("Budget updated successfully", request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to update budget: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteBudget(@PathVariable Long id, Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Budget budget = budgetRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Budget not found"));

            if (!budget.getUserId().equals(user.getId())) {
                return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
            }

            budgetRepository.deleteBudget(id);
            return ResponseEntity.ok(ApiResponse.success("Budget deleted successfully", "Budget deleted"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to delete budget: " + e.getMessage()));
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<List<Budget>>> getBudgetSummary(Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Budget> budgets = budgetRepository.findByUserId(user.getId());
            return ResponseEntity.ok(ApiResponse.success("Budget summary retrieved successfully", budgets));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve budget summary: " + e.getMessage()));
        }
    }
} 