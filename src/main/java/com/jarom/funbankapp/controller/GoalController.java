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
import com.jarom.funbankapp.dto.GoalDTO;
import com.jarom.funbankapp.dto.GoalUpdateRequest;
import com.jarom.funbankapp.model.Goal;
import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.GoalRepository;
import com.jarom.funbankapp.repository.UserRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/goals")
@Tag(name = "Goals", description = "Goal management operations")
@SecurityRequirement(name = "bearerAuth")
public class GoalController {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    public GoalController(GoalRepository goalRepository, UserRepository userRepository) {
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<GoalDTO>> createGoal(@RequestBody GoalDTO request, Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Goal goal = new Goal();
            goal.setUserId(user.getId());
            goal.setName(request.getName());
            goal.setDescription(request.getDescription());
            goal.setTargetAmount(request.getTargetAmount());
            goal.setCurrentAmount(request.getCurrentAmount() != null ? request.getCurrentAmount() : request.getTargetAmount().multiply(java.math.BigDecimal.ZERO));
            goal.setDeadline(request.getDeadline() != null ? Timestamp.valueOf(request.getDeadline().atStartOfDay()) : null);
            goal.setType(request.getType());
            goal.setStatus(request.getStatus() != null ? request.getStatus() : "active");
            
            goalRepository.createGoal(goal);
            
            // Convert back to DTO for response
            GoalDTO response = convertToDTO(goal);
            return ResponseEntity.ok(ApiResponse.success("Goal created successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to create goal: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<GoalDTO>>> getGoals(Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Goal> goals = goalRepository.findByUserId(user.getId());
            List<GoalDTO> goalDTOs = goals.stream().map(this::convertToDTO).collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("Goals retrieved successfully", goalDTOs));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve goals: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GoalDTO>> getGoal(@PathVariable Long id, Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Goal goal = goalRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Goal not found"));

            if (!goal.getUserId().equals(user.getId())) {
                return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
            }

            GoalDTO goalDTO = convertToDTO(goal);
            return ResponseEntity.ok(ApiResponse.success("Goal retrieved successfully", goalDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve goal: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<GoalDTO>> updateGoal(@PathVariable Long id, @RequestBody GoalDTO request, Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Goal existingGoal = goalRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Goal not found"));

            if (!existingGoal.getUserId().equals(user.getId())) {
                return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
            }

            existingGoal.setName(request.getName());
            existingGoal.setDescription(request.getDescription());
            existingGoal.setTargetAmount(request.getTargetAmount());
            existingGoal.setCurrentAmount(request.getCurrentAmount());
            existingGoal.setDeadline(request.getDeadline() != null ? Timestamp.valueOf(request.getDeadline().atStartOfDay()) : null);
            existingGoal.setType(request.getType());
            existingGoal.setStatus(request.getStatus());
            
            goalRepository.updateGoal(existingGoal);

            GoalDTO response = convertToDTO(existingGoal);
            return ResponseEntity.ok(ApiResponse.success("Goal updated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to update goal: " + e.getMessage()));
        }
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update goal", description = "Update specific fields of an existing goal")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Goal partially updated successfully",
            content = @Content(schema = @Schema(implementation = GoalDTO.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid goal data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Unauthorized: You don't own this goal"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Goal not found")
    })
    public ResponseEntity<ApiResponse<GoalDTO>> patchGoal(
            @Parameter(description = "Goal ID to update", example = "1")
            @PathVariable Long id,
            @RequestBody GoalUpdateRequest request,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Goal existingGoal = goalRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Goal not found"));

            if (!existingGoal.getUserId().equals(user.getId())) {
                return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
            }

            // Apply partial updates
            if (request.getName() != null) {
                existingGoal.setName(request.getName());
            }
            if (request.getDescription() != null) {
                existingGoal.setDescription(request.getDescription());
            }
            if (request.getTargetAmount() != null) {
                existingGoal.setTargetAmount(request.getTargetAmount());
            }
            if (request.getCurrentAmount() != null) {
                existingGoal.setCurrentAmount(request.getCurrentAmount());
            }
            if (request.getDeadline() != null) {
                existingGoal.setDeadline(Timestamp.valueOf(request.getDeadline().atStartOfDay()));
            }
            if (request.getType() != null) {
                existingGoal.setType(request.getType());
            }
            if (request.getStatus() != null) {
                existingGoal.setStatus(request.getStatus());
            }
            
            goalRepository.updateGoal(existingGoal);

            GoalDTO response = convertToDTO(existingGoal);
            return ResponseEntity.ok(ApiResponse.success("Goal partially updated successfully", response));
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
    public ResponseEntity<ApiResponse<List<GoalDTO>>> getGoalSummary(Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Goal> goals = goalRepository.findByUserId(user.getId());
            List<GoalDTO> goalDTOs = goals.stream().map(this::convertToDTO).collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("Goal summary retrieved successfully", goalDTOs));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve goal summary: " + e.getMessage()));
        }
    }

    private GoalDTO convertToDTO(Goal goal) {
        GoalDTO dto = new GoalDTO();
        dto.setId(goal.getId());
        dto.setUserId(goal.getUserId());
        dto.setName(goal.getName());
        dto.setDescription(goal.getDescription());
        dto.setTargetAmount(goal.getTargetAmount());
        dto.setCurrentAmount(goal.getCurrentAmount());
        dto.setDeadline(goal.getDeadline() != null ? goal.getDeadline().toLocalDateTime().toLocalDate() : null);
        dto.setType(goal.getType());
        dto.setStatus(goal.getStatus());
        dto.setCreatedAt(goal.getCreatedAt() != null ? goal.getCreatedAt().toLocalDateTime() : null);
        dto.setUpdatedAt(goal.getUpdatedAt() != null ? goal.getUpdatedAt().toLocalDateTime() : null);
        return dto;
    }
} 