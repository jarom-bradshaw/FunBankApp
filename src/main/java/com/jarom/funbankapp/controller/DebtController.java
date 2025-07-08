package com.jarom.funbankapp.controller;

import java.math.BigDecimal;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jarom.funbankapp.dto.DebtDTO;
import com.jarom.funbankapp.dto.DebtPaymentDTO;
import com.jarom.funbankapp.service.DebtService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/debts")
@Tag(name = "Debts", description = "Debt management operations")
@SecurityRequirement(name = "bearerAuth")
public class DebtController {

    private final DebtService debtService;

    public DebtController(DebtService debtService) {
        this.debtService = debtService;
    }

    @PostMapping
    @Operation(
        summary = "Create a new debt", 
        description = "Creates a new debt for the authenticated user. The debt will be associated with the current user's ID."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Debt created successfully",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid debt data provided"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<com.jarom.funbankapp.dto.ApiResponse<DebtDTO>> createDebt(
        @Parameter(description = "Debt details to create", required = true)
        @Valid @RequestBody DebtDTO request
    ) {
        DebtDTO createdDebt = debtService.createDebt(request);
        return ResponseEntity.ok(com.jarom.funbankapp.dto.ApiResponse.success("Debt created successfully", createdDebt));
    }

    @GetMapping
    @Operation(
        summary = "Get user debts", 
        description = "Retrieves all debts for the authenticated user. Returns a list of debt objects with balances and details."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Debts retrieved successfully",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<com.jarom.funbankapp.dto.ApiResponse<List<DebtDTO>>> getUserDebts() {
        List<DebtDTO> debts = debtService.getUserDebts();
        return ResponseEntity.ok(com.jarom.funbankapp.dto.ApiResponse.success("Debts retrieved successfully", debts));
    }

    @GetMapping("/{debtId}")
    @Operation(
        summary = "Get debt by ID", 
        description = "Retrieves a specific debt by ID. The debt must belong to the authenticated user."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Debt retrieved successfully",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Unauthorized: You don't own this debt"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Debt not found")
    })
    public ResponseEntity<com.jarom.funbankapp.dto.ApiResponse<DebtDTO>> getDebtById(
        @Parameter(description = "Debt ID to retrieve", example = "1")
        @PathVariable Long debtId
    ) {
        DebtDTO debt = debtService.getDebtById(debtId);
        return ResponseEntity.ok(com.jarom.funbankapp.dto.ApiResponse.success("Debt retrieved successfully", debt));
    }

    @PutMapping("/{debtId}")
    @Operation(
        summary = "Update debt", 
        description = "Updates an existing debt. The debt must belong to the authenticated user."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Debt updated successfully",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid debt data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Unauthorized: You don't own this debt"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Debt not found")
    })
    public ResponseEntity<com.jarom.funbankapp.dto.ApiResponse<DebtDTO>> updateDebt(
        @Parameter(description = "Debt ID to update", example = "1")
        @PathVariable Long debtId,
        @Parameter(description = "Updated debt data", required = true)
        @Valid @RequestBody DebtDTO request
    ) {
        DebtDTO updatedDebt = debtService.updateDebt(debtId, request);
        return ResponseEntity.ok(com.jarom.funbankapp.dto.ApiResponse.success("Debt updated successfully", updatedDebt));
    }

    @DeleteMapping("/{debtId}")
    @Operation(
        summary = "Delete debt", 
        description = "Deletes a debt. The debt must have zero balance and belong to the authenticated user."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Debt deleted successfully",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Debt has remaining balance"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Unauthorized: You don't own this debt"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Debt not found")
    })
    public ResponseEntity<com.jarom.funbankapp.dto.ApiResponse<String>> deleteDebt(
        @Parameter(description = "Debt ID to delete", example = "1")
        @PathVariable Long debtId
    ) {
        debtService.deleteDebt(debtId);
        return ResponseEntity.ok(com.jarom.funbankapp.dto.ApiResponse.success("Debt deleted successfully", 
            "Debt with ID " + debtId + " has been deleted"));
    }

    @PostMapping("/{debtId}/payments")
    @Operation(
        summary = "Make a payment", 
        description = "Makes a payment towards a debt. The debt must belong to the authenticated user."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Payment successful",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid payment amount or exceeds balance"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Unauthorized: You don't own this debt"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Debt not found")
    })
    public ResponseEntity<com.jarom.funbankapp.dto.ApiResponse<DebtDTO>> makePayment(
        @Parameter(description = "Debt ID to make payment on", example = "1")
        @PathVariable Long debtId,
        @Parameter(description = "Payment details", required = true)
        @Valid @RequestBody DebtPaymentDTO request
    ) {
        DebtDTO updatedDebt = debtService.makePayment(debtId, request);
        return ResponseEntity.ok(com.jarom.funbankapp.dto.ApiResponse.success("Payment successful", updatedDebt));
    }

    @GetMapping("/{debtId}/payments")
    @Operation(
        summary = "Get payment history", 
        description = "Retrieves payment history for a specific debt. The debt must belong to the authenticated user."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Payment history retrieved successfully",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Unauthorized: You don't own this debt"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Debt not found")
    })
    public ResponseEntity<com.jarom.funbankapp.dto.ApiResponse<List<DebtPaymentDTO>>> getPaymentHistory(
        @Parameter(description = "Debt ID to get payment history for", example = "1")
        @PathVariable Long debtId
    ) {
        List<DebtPaymentDTO> payments = debtService.getPaymentHistory(debtId);
        return ResponseEntity.ok(com.jarom.funbankapp.dto.ApiResponse.success("Payment history retrieved successfully", payments));
    }

    @GetMapping("/summary")
    @Operation(
        summary = "Get debt summary", 
        description = "Retrieves a summary of all debts for the authenticated user including totals and breakdowns."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Debt summary retrieved successfully",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<com.jarom.funbankapp.dto.ApiResponse<Map<String, Object>>> getDebtSummary() {
        Map<String, Object> summary = debtService.getDebtSummary();
        return ResponseEntity.ok(com.jarom.funbankapp.dto.ApiResponse.success("Debt summary retrieved successfully", summary));
    }

    @GetMapping("/analysis")
    @Operation(
        summary = "Get debt analysis", 
        description = "Retrieves detailed analysis of debts including insights and recommendations."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Debt analysis retrieved successfully",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<com.jarom.funbankapp.dto.ApiResponse<Map<String, Object>>> getDebtAnalysis() {
        Map<String, Object> analysis = debtService.getDebtAnalysis();
        return ResponseEntity.ok(com.jarom.funbankapp.dto.ApiResponse.success("Debt analysis retrieved successfully", analysis));
    }

    @GetMapping("/payoff-timeline")
    @Operation(
        summary = "Calculate payoff timeline", 
        description = "Calculates debt payoff timeline using different strategies (snowball or avalanche)."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Payoff timeline calculated successfully",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid parameters"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<com.jarom.funbankapp.dto.ApiResponse<Map<String, Object>>> calculatePayoffTimeline(
        @Parameter(description = "Monthly payment amount available for debt repayment", example = "1000.00")
        @RequestParam BigDecimal monthlyPaymentAmount,
        @Parameter(description = "Repayment strategy", example = "snowball")
        @RequestParam String strategy
    ) {
        Map<String, Object> timeline = debtService.calculatePayoffTimeline(monthlyPaymentAmount, strategy);
        return ResponseEntity.ok(com.jarom.funbankapp.dto.ApiResponse.success("Payoff timeline calculated successfully", timeline));
    }

    @GetMapping("/by-type")
    @Operation(
        summary = "Get debts by type", 
        description = "Retrieves debts filtered by type (credit_card, student_loan, mortgage, etc.)."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Debts retrieved successfully",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid debt type"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<com.jarom.funbankapp.dto.ApiResponse<List<DebtDTO>>> getDebtsByType(
        @Parameter(description = "Debt type to filter by", example = "credit_card")
        @RequestParam String debtType
    ) {
        List<DebtDTO> debts = debtService.getDebtsByType(debtType);
        return ResponseEntity.ok(com.jarom.funbankapp.dto.ApiResponse.success("Debts retrieved successfully", debts));
    }

    @GetMapping("/by-priority")
    @Operation(
        summary = "Get debts by priority", 
        description = "Retrieves debts filtered by priority level (high, medium, low)."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Debts retrieved successfully",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid priority level"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<com.jarom.funbankapp.dto.ApiResponse<List<DebtDTO>>> getDebtsByPriority(
        @Parameter(description = "Priority level to filter by", example = "high")
        @RequestParam String priority
    ) {
        List<DebtDTO> debts = debtService.getDebtsByPriority(priority);
        return ResponseEntity.ok(com.jarom.funbankapp.dto.ApiResponse.success("Debts retrieved successfully", debts));
    }

    @GetMapping("/total-amount")
    @Operation(
        summary = "Get total debt amount", 
        description = "Retrieves the total debt amount for the authenticated user."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Total debt amount retrieved successfully",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<com.jarom.funbankapp.dto.ApiResponse<BigDecimal>> getTotalDebtAmount() {
        BigDecimal totalAmount = debtService.getTotalDebtAmount();
        return ResponseEntity.ok(com.jarom.funbankapp.dto.ApiResponse.success("Total debt amount retrieved successfully", totalAmount));
    }

    @GetMapping("/total-minimum-payments")
    @Operation(
        summary = "Get total minimum payments", 
        description = "Retrieves the total minimum monthly payments for all debts."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Total minimum payments retrieved successfully",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<com.jarom.funbankapp.dto.ApiResponse<BigDecimal>> getTotalMinimumPayments() {
        BigDecimal totalMinimumPayments = debtService.getTotalMinimumPayments();
        return ResponseEntity.ok(com.jarom.funbankapp.dto.ApiResponse.success("Total minimum payments retrieved successfully", totalMinimumPayments));
    }
} 