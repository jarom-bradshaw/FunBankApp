package com.jarom.funbankapp.controller;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jarom.funbankapp.dto.ApiResponse;
import com.jarom.funbankapp.dto.BudgetDTO;
import com.jarom.funbankapp.dto.BudgetUpdateRequest;
import com.jarom.funbankapp.model.Budget;
import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.BudgetRepository;
import com.jarom.funbankapp.repository.UserRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/budgets")
@Tag(name = "Budgets", description = "Budget management operations")
@SecurityRequirement(name = "bearerAuth")
public class BudgetController {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;

    public BudgetController(BudgetRepository budgetRepository, UserRepository userRepository) {
        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BudgetDTO>> createBudget(@RequestBody BudgetDTO request, Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Budget budget = new Budget();
            budget.setUserId(user.getId());
            budget.setName(request.getName());
            budget.setCategory(request.getCategory());
            budget.setAmount(request.getAmount());
            budget.setPeriod(request.getPeriod());
            budget.setDescription(request.getDescription());
            budget.setSpent(request.getSpent() != null ? request.getSpent() : java.math.BigDecimal.ZERO);
            budget.setStartDate(request.getStartDate() != null ? Timestamp.valueOf(request.getStartDate().atStartOfDay()) : null);
            budget.setEndDate(request.getEndDate() != null ? Timestamp.valueOf(request.getEndDate().atStartOfDay()) : null);
            
            budgetRepository.createBudget(budget);
            
            // Convert back to DTO for response
            BudgetDTO response = convertToDTO(budget);
            return ResponseEntity.ok(ApiResponse.success("Budget created successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to create budget: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BudgetDTO>>> getBudgets(Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Budget> budgets = budgetRepository.findByUserId(user.getId());
            List<BudgetDTO> budgetDTOs = budgets.stream().map(this::convertToDTO).collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("Budgets retrieved successfully", budgetDTOs));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve budgets: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BudgetDTO>> getBudget(@PathVariable Long id, Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Budget budget = budgetRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Budget not found"));

            if (!budget.getUserId().equals(user.getId())) {
                return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
            }

            BudgetDTO budgetDTO = convertToDTO(budget);
            return ResponseEntity.ok(ApiResponse.success("Budget retrieved successfully", budgetDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve budget: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BudgetDTO>> updateBudget(@PathVariable Long id, @RequestBody BudgetDTO request, Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Budget existingBudget = budgetRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Budget not found"));

            if (!existingBudget.getUserId().equals(user.getId())) {
                return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
            }

            existingBudget.setName(request.getName());
            existingBudget.setCategory(request.getCategory());
            existingBudget.setAmount(request.getAmount());
            existingBudget.setPeriod(request.getPeriod());
            existingBudget.setDescription(request.getDescription());
            existingBudget.setSpent(request.getSpent());
            existingBudget.setStartDate(request.getStartDate() != null ? Timestamp.valueOf(request.getStartDate().atStartOfDay()) : null);
            existingBudget.setEndDate(request.getEndDate() != null ? Timestamp.valueOf(request.getEndDate().atStartOfDay()) : null);
            
            budgetRepository.updateBudget(existingBudget);

            BudgetDTO response = convertToDTO(existingBudget);
            return ResponseEntity.ok(ApiResponse.success("Budget updated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to update budget: " + e.getMessage()));
        }
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update budget", description = "Update specific fields of an existing budget")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Budget partially updated successfully",
            content = @Content(schema = @Schema(implementation = BudgetDTO.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid budget data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Unauthorized: You don't own this budget"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Budget not found")
    })
    public ResponseEntity<ApiResponse<BudgetDTO>> patchBudget(
            @Parameter(description = "Budget ID to update", example = "1")
            @PathVariable Long id,
            @RequestBody BudgetUpdateRequest request,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Budget existingBudget = budgetRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Budget not found"));

            if (!existingBudget.getUserId().equals(user.getId())) {
                return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
            }

            // Apply partial updates
            if (request.getName() != null) {
                existingBudget.setName(request.getName());
            }
            if (request.getCategory() != null) {
                existingBudget.setCategory(request.getCategory());
            }
            if (request.getAmount() != null) {
                existingBudget.setAmount(request.getAmount());
            }
            if (request.getPeriod() != null) {
                existingBudget.setPeriod(request.getPeriod());
            }
            if (request.getDescription() != null) {
                existingBudget.setDescription(request.getDescription());
            }
            if (request.getSpent() != null) {
                existingBudget.setSpent(request.getSpent());
            }
            if (request.getStartDate() != null) {
                existingBudget.setStartDate(Timestamp.valueOf(request.getStartDate().atStartOfDay()));
            }
            if (request.getEndDate() != null) {
                existingBudget.setEndDate(Timestamp.valueOf(request.getEndDate().atStartOfDay()));
            }
            
            budgetRepository.updateBudget(existingBudget);

            BudgetDTO response = convertToDTO(existingBudget);
            return ResponseEntity.ok(ApiResponse.success("Budget partially updated successfully", response));
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
    public ResponseEntity<ApiResponse<List<BudgetDTO>>> getBudgetSummary(Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Budget> budgets = budgetRepository.findByUserId(user.getId());
            List<BudgetDTO> budgetDTOs = budgets.stream().map(this::convertToDTO).collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("Budget summary retrieved successfully", budgetDTOs));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve budget summary: " + e.getMessage()));
        }
    }

    private BudgetDTO convertToDTO(Budget budget) {
        BudgetDTO dto = new BudgetDTO();
        dto.setId(budget.getId());
        dto.setUserId(budget.getUserId());
        dto.setName(budget.getName());
        dto.setCategory(budget.getCategory());
        dto.setAmount(budget.getAmount());
        dto.setPeriod(budget.getPeriod());
        dto.setDescription(budget.getDescription());
        dto.setSpent(budget.getSpent());
        dto.setStartDate(budget.getStartDate() != null ? budget.getStartDate().toLocalDateTime().toLocalDate() : null);
        dto.setEndDate(budget.getEndDate() != null ? budget.getEndDate().toLocalDateTime().toLocalDate() : null);
        dto.setCreatedAt(budget.getCreatedAt() != null ? budget.getCreatedAt().toLocalDateTime() : null);
        dto.setUpdatedAt(budget.getUpdatedAt() != null ? budget.getUpdatedAt().toLocalDateTime() : null);
        return dto;
    }
} 