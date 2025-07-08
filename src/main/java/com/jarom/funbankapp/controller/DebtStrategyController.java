package com.jarom.funbankapp.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jarom.funbankapp.dto.DebtStrategyDTO;
import com.jarom.funbankapp.service.DebtStrategyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/debt-strategies")
@Tag(name = "Debt Strategies", description = "Debt repayment strategy management")
@SecurityRequirement(name = "bearerAuth")
public class DebtStrategyController {

    private final DebtStrategyService debtStrategyService;

    public DebtStrategyController(DebtStrategyService debtStrategyService) {
        this.debtStrategyService = debtStrategyService;
    }

    @PostMapping
    @Operation(
        summary = "Create a new debt strategy", 
        description = "Creates a new debt repayment strategy for the authenticated user."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Strategy created successfully",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid strategy data provided"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<com.jarom.funbankapp.dto.ApiResponse<DebtStrategyDTO>> createStrategy(
        @Parameter(description = "Strategy details to create", required = true)
        @Valid @RequestBody DebtStrategyDTO request
    ) {
        DebtStrategyDTO createdStrategy = debtStrategyService.createStrategy(request);
        return ResponseEntity.ok(com.jarom.funbankapp.dto.ApiResponse.success("Strategy created successfully", createdStrategy));
    }

    @GetMapping
    @Operation(
        summary = "Get user strategies", 
        description = "Retrieves all debt repayment strategies for the authenticated user."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Strategies retrieved successfully",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<com.jarom.funbankapp.dto.ApiResponse<List<DebtStrategyDTO>>> getUserStrategies() {
        List<DebtStrategyDTO> strategies = debtStrategyService.getUserStrategies();
        return ResponseEntity.ok(com.jarom.funbankapp.dto.ApiResponse.success("Strategies retrieved successfully", strategies));
    }

    @GetMapping("/{strategyId}")
    @Operation(
        summary = "Get strategy by ID", 
        description = "Retrieves a specific debt strategy by ID. The strategy must belong to the authenticated user."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Strategy retrieved successfully",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Unauthorized: You don't own this strategy"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Strategy not found")
    })
    public ResponseEntity<com.jarom.funbankapp.dto.ApiResponse<DebtStrategyDTO>> getStrategyById(
        @Parameter(description = "Strategy ID to retrieve", example = "1")
        @PathVariable Long strategyId
    ) {
        DebtStrategyDTO strategy = debtStrategyService.getStrategyById(strategyId);
        return ResponseEntity.ok(com.jarom.funbankapp.dto.ApiResponse.success("Strategy retrieved successfully", strategy));
    }

    @PutMapping("/{strategyId}")
    @Operation(
        summary = "Update strategy", 
        description = "Updates an existing debt strategy. The strategy must belong to the authenticated user."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Strategy updated successfully",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid strategy data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Unauthorized: You don't own this strategy"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Strategy not found")
    })
    public ResponseEntity<com.jarom.funbankapp.dto.ApiResponse<DebtStrategyDTO>> updateStrategy(
        @Parameter(description = "Strategy ID to update", example = "1")
        @PathVariable Long strategyId,
        @Parameter(description = "Updated strategy data", required = true)
        @Valid @RequestBody DebtStrategyDTO request
    ) {
        DebtStrategyDTO updatedStrategy = debtStrategyService.updateStrategy(strategyId, request);
        return ResponseEntity.ok(com.jarom.funbankapp.dto.ApiResponse.success("Strategy updated successfully", updatedStrategy));
    }

    @DeleteMapping("/{strategyId}")
    @Operation(
        summary = "Delete strategy", 
        description = "Deletes a debt strategy. The strategy must belong to the authenticated user."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Strategy deleted successfully",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Unauthorized: You don't own this strategy"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Strategy not found")
    })
    public ResponseEntity<com.jarom.funbankapp.dto.ApiResponse<String>> deleteStrategy(
        @Parameter(description = "Strategy ID to delete", example = "1")
        @PathVariable Long strategyId
    ) {
        debtStrategyService.deleteStrategy(strategyId);
        return ResponseEntity.ok(com.jarom.funbankapp.dto.ApiResponse.success("Strategy deleted successfully", 
            "Strategy with ID " + strategyId + " has been deleted"));
    }

    @PostMapping("/{strategyId}/activate")
    @Operation(
        summary = "Activate strategy", 
        description = "Activates a debt strategy and deactivates all other strategies for the user."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Strategy activated successfully",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Unauthorized: You don't own this strategy"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Strategy not found")
    })
    public ResponseEntity<com.jarom.funbankapp.dto.ApiResponse<DebtStrategyDTO>> activateStrategy(
        @Parameter(description = "Strategy ID to activate", example = "1")
        @PathVariable Long strategyId
    ) {
        DebtStrategyDTO activatedStrategy = debtStrategyService.activateStrategy(strategyId);
        return ResponseEntity.ok(com.jarom.funbankapp.dto.ApiResponse.success("Strategy activated successfully", activatedStrategy));
    }

    @GetMapping("/active")
    @Operation(
        summary = "Get active strategy", 
        description = "Retrieves the currently active debt strategy for the authenticated user."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Active strategy retrieved successfully",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<com.jarom.funbankapp.dto.ApiResponse<DebtStrategyDTO>> getActiveStrategy() {
        DebtStrategyDTO activeStrategy = debtStrategyService.getActiveStrategy();
        if (activeStrategy != null) {
            return ResponseEntity.ok(com.jarom.funbankapp.dto.ApiResponse.success("Active strategy retrieved successfully", activeStrategy));
        } else {
            return ResponseEntity.ok(com.jarom.funbankapp.dto.ApiResponse.success("No active strategy found", null));
        }
    }

    @GetMapping("/generate/snowball")
    @Operation(
        summary = "Generate snowball strategy", 
        description = "Generates a snowball debt repayment strategy recommendation based on user's debts."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Snowball strategy generated successfully",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<com.jarom.funbankapp.dto.ApiResponse<Map<String, Object>>> generateSnowballStrategy() {
        Map<String, Object> strategy = debtStrategyService.generateSnowballStrategy();
        return ResponseEntity.ok(com.jarom.funbankapp.dto.ApiResponse.success("Snowball strategy generated successfully", strategy));
    }

    @GetMapping("/generate/avalanche")
    @Operation(
        summary = "Generate avalanche strategy", 
        description = "Generates an avalanche debt repayment strategy recommendation based on user's debts."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Avalanche strategy generated successfully",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<com.jarom.funbankapp.dto.ApiResponse<Map<String, Object>>> generateAvalancheStrategy() {
        Map<String, Object> strategy = debtStrategyService.generateAvalancheStrategy();
        return ResponseEntity.ok(com.jarom.funbankapp.dto.ApiResponse.success("Avalanche strategy generated successfully", strategy));
    }

    @GetMapping("/{strategyId}/effectiveness")
    @Operation(
        summary = "Calculate strategy effectiveness", 
        description = "Calculates the effectiveness of a specific debt strategy."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Strategy effectiveness calculated successfully",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Unauthorized: You don't own this strategy"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Strategy not found")
    })
    public ResponseEntity<com.jarom.funbankapp.dto.ApiResponse<Map<String, Object>>> calculateStrategyEffectiveness(
        @Parameter(description = "Strategy ID to analyze", example = "1")
        @PathVariable Long strategyId
    ) {
        Map<String, Object> effectiveness = debtStrategyService.calculateStrategyEffectiveness(strategyId);
        return ResponseEntity.ok(com.jarom.funbankapp.dto.ApiResponse.success("Strategy effectiveness calculated successfully", effectiveness));
    }

    @GetMapping("/compare")
    @Operation(
        summary = "Compare strategies", 
        description = "Compares different debt repayment strategies (snowball vs avalanche) for the user's debts."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Strategy comparison completed successfully",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<com.jarom.funbankapp.dto.ApiResponse<Map<String, Object>>> compareStrategies() {
        Map<String, Object> comparison = debtStrategyService.compareStrategies();
        return ResponseEntity.ok(com.jarom.funbankapp.dto.ApiResponse.success("Strategy comparison completed successfully", comparison));
    }
} 