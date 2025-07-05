package com.jarom.funbankapp.controller;

import com.jarom.funbankapp.model.Budget;
import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.BudgetDAO;
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
@RequestMapping("/api/budgets")
@io.swagger.v3.oas.annotations.tags.Tag(name = "Budgets", description = "Budget management operations")
public class BudgetController {

    private final BudgetDAO budgetDAO;
    private final UserDAO userDAO;

    public BudgetController(BudgetDAO budgetDAO, UserDAO userDAO) {
        this.budgetDAO = budgetDAO;
        this.userDAO = userDAO;
    }

    @PostMapping
    @Operation(summary = "Create a new budget", description = "Creates a new budget for the authenticated user.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Budget created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid budget data")
    })
    public ResponseEntity<?> createBudget(@RequestBody Budget request) {
        String username = getCurrentUsername();
        User user = userDAO.findByUsername(username);

        request.setUserId(user.getId());
        request.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        request.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        budgetDAO.createBudget(request);
        return ResponseEntity.ok("Budget created successfully!");
    }

    @GetMapping
    @Operation(summary = "Get user budgets", description = "Retrieves all budgets for the authenticated user.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Budgets retrieved successfully")
    })
    public ResponseEntity<List<Budget>> getUserBudgets() {
        String username = getCurrentUsername();
        User user = userDAO.findByUsername(username);

        List<Budget> budgets = budgetDAO.findByUserId(user.getId());
        return ResponseEntity.ok(budgets);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get budget by ID", description = "Retrieves a specific budget by ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Budget retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Budget not found"),
        @ApiResponse(responseCode = "403", description = "Unauthorized: You don't own this budget")
    })
    public ResponseEntity<?> getBudget(@PathVariable Long id) {
        String username = getCurrentUsername();
        User user = userDAO.findByUsername(username);

        Budget budget = budgetDAO.findById(id);
        if (budget == null) {
            return ResponseEntity.notFound().build();
        }

        if (budget.getUserId() != user.getId()) {
            return ResponseEntity.status(403).body("Unauthorized: You don't own this budget.");
        }

        return ResponseEntity.ok(budget);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update budget", description = "Updates an existing budget.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Budget updated successfully"),
        @ApiResponse(responseCode = "404", description = "Budget not found"),
        @ApiResponse(responseCode = "403", description = "Unauthorized: You don't own this budget")
    })
    public ResponseEntity<?> updateBudget(@PathVariable Long id, @RequestBody Budget request) {
        String username = getCurrentUsername();
        User user = userDAO.findByUsername(username);

        Budget existingBudget = budgetDAO.findById(id);
        if (existingBudget == null) {
            return ResponseEntity.notFound().build();
        }

        if (existingBudget.getUserId() != user.getId()) {
            return ResponseEntity.status(403).body("Unauthorized: You don't own this budget.");
        }

        request.setId(id);
        request.setUserId(user.getId());
        request.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        budgetDAO.updateBudget(request);
        return ResponseEntity.ok("Budget updated successfully!");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete budget", description = "Deletes a budget.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Budget deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Budget not found"),
        @ApiResponse(responseCode = "403", description = "Unauthorized: You don't own this budget")
    })
    public ResponseEntity<?> deleteBudget(@PathVariable Long id) {
        String username = getCurrentUsername();
        User user = userDAO.findByUsername(username);

        Budget budget = budgetDAO.findById(id);
        if (budget == null) {
            return ResponseEntity.notFound().build();
        }

        if (budget.getUserId() != user.getId()) {
            return ResponseEntity.status(403).body("Unauthorized: You don't own this budget.");
        }

        budgetDAO.deleteBudget(id);
        return ResponseEntity.ok("Budget deleted successfully!");
    }

    @GetMapping("/summary")
    @Operation(summary = "Get budget summary", description = "Retrieves a summary of all budgets for the authenticated user.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Budget summary retrieved successfully")
    })
    public ResponseEntity<?> getBudgetSummary() {
        String username = getCurrentUsername();
        User user = userDAO.findByUsername(username);

        List<Budget> budgets = budgetDAO.findByUserId(user.getId());
        
        BigDecimal totalBudgeted = budgets.stream()
                .map(Budget::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ResponseEntity.ok(new BudgetSummary(totalBudgeted, budgets));
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    // Inner class for budget summary response
    public static class BudgetSummary {
        private BigDecimal totalBudgeted;
        private List<Budget> budgets;

        public BudgetSummary(BigDecimal totalBudgeted, List<Budget> budgets) {
            this.totalBudgeted = totalBudgeted;
            this.budgets = budgets;
        }

        // Getters
        public BigDecimal getTotalBudgeted() { return totalBudgeted; }
        public List<Budget> getBudgets() { return budgets; }
    }
} 