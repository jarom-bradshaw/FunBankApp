package com.jarom.funbankapp.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jarom.funbankapp.dto.ApiResponse;
import com.jarom.funbankapp.dto.TransactionDTO;
import com.jarom.funbankapp.dto.TransactionRequest;
import com.jarom.funbankapp.dto.TransactionUpdateRequest;
import com.jarom.funbankapp.dto.TransferRequest;
import com.jarom.funbankapp.service.TransactionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transactions", description = "Transaction management and history")
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    @Operation(summary = "Get recent transactions", description = "Retrieve recent transactions for the authenticated user")
    public ResponseEntity<ApiResponse<List<TransactionDTO>>> getTransactions(
            @Parameter(description = "Maximum number of transactions to return", example = "100")
            @RequestParam(defaultValue = "100") int limit) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            List<TransactionDTO> transactions = transactionService.getRecentTransactions(username, limit);
            return ResponseEntity.ok(ApiResponse.success("Transactions retrieved successfully", transactions));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve transactions: " + e.getMessage()));
        }
    }

    @PostMapping
    @Operation(summary = "Create transaction", description = "Create a new transaction")
    public ResponseEntity<ApiResponse<TransactionDTO>> createTransaction(
            @Valid @RequestBody TransactionRequest request) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            TransactionDTO transaction = transactionService.createTransaction(username, request);
            return ResponseEntity.ok(ApiResponse.success("Transaction created successfully", transaction));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to create transaction: " + e.getMessage()));
        }
    }

    @PostMapping("/deposit")
    @Operation(summary = "Create deposit", description = "Create a deposit transaction")
    public ResponseEntity<ApiResponse<TransactionDTO>> deposit(
            @Valid @RequestBody TransactionRequest request) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            request.setType("deposit");
            TransactionDTO transaction = transactionService.createTransaction(username, request);
            return ResponseEntity.ok(ApiResponse.success("Deposit successful", transaction));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to process deposit: " + e.getMessage()));
        }
    }

    @PostMapping("/withdraw")
    @Operation(summary = "Create withdrawal", description = "Create a withdrawal transaction")
    public ResponseEntity<ApiResponse<TransactionDTO>> withdraw(
            @Valid @RequestBody TransactionRequest request) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            request.setType("withdraw");
            TransactionDTO transaction = transactionService.createTransaction(username, request);
            return ResponseEntity.ok(ApiResponse.success("Withdrawal successful", transaction));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to process withdrawal: " + e.getMessage()));
        }
    }

    @PostMapping("/transfer")
    @Operation(summary = "Transfer between accounts", description = "Transfer funds between two accounts")
    public ResponseEntity<ApiResponse<Map<String, TransactionDTO>>> transfer(
            @Valid @RequestBody TransferRequest request) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Map<String, TransactionDTO> result = transactionService.transferBetweenAccounts(username, request);
            return ResponseEntity.ok(ApiResponse.success("Transfer completed successfully", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to process transfer: " + e.getMessage()));
        }
    }

    @GetMapping("/account/{accountId}")
    @Operation(summary = "Get transactions by account", description = "Retrieve transactions for a specific account")
    public ResponseEntity<ApiResponse<List<TransactionDTO>>> getTransactionsByAccount(
            @Parameter(description = "Account ID", example = "1")
            @PathVariable Long accountId) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            List<TransactionDTO> transactions = transactionService.getTransactionsByAccount(username, accountId);
            return ResponseEntity.ok(ApiResponse.success("Account transactions retrieved successfully", transactions));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve account transactions: " + e.getMessage()));
        }
    }

    @GetMapping("/spending")
    @Operation(summary = "Get spending by category", description = "Retrieve spending breakdown by category")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getSpendingByCategory(
            @Parameter(description = "Number of days to look back", example = "30")
            @RequestParam(defaultValue = "30") int days) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Map<String, BigDecimal> categories = transactionService.getSpendingByCategory(username, days);
            return ResponseEntity.ok(ApiResponse.success("Spending by category retrieved successfully", categories));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve spending data: " + e.getMessage()));
        }
    }

    @GetMapping("/summary")
    @Operation(summary = "Get transaction summary", description = "Retrieve transaction summary with spending breakdown")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTransactionSummary(
            @Parameter(description = "Number of days to look back", example = "30")
            @RequestParam(defaultValue = "30") int days) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Map<String, Object> summary = transactionService.getTransactionSummary(username, days);
            return ResponseEntity.ok(ApiResponse.success("Transaction summary retrieved successfully", summary));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve transaction summary: " + e.getMessage()));
        }
    }

    @GetMapping("/trends")
    @Operation(summary = "Get transaction trends", description = "Retrieve transaction trends over time")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTransactionTrends(
            @Parameter(description = "Number of months to analyze", example = "6")
            @RequestParam(defaultValue = "6") int months) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Map<String, Object> trends = transactionService.getTransactionTrends(username, months);
            return ResponseEntity.ok(ApiResponse.success("Transaction trends retrieved successfully", trends));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve transaction trends: " + e.getMessage()));
        }
    }

    @GetMapping("/monthly")
    @Operation(summary = "Get monthly analysis", description = "Retrieve monthly transaction analysis")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMonthlyAnalysis(
            @Parameter(description = "Year to analyze", example = "2024")
            @RequestParam(defaultValue = "2024") int year) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Map<String, Object> monthly = transactionService.getMonthlyAnalysis(username, year);
            return ResponseEntity.ok(ApiResponse.success("Monthly analysis retrieved successfully", monthly));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve monthly analysis: " + e.getMessage()));
        }
    }

    @GetMapping("/{transactionId}")
    @Operation(summary = "Get transaction by ID", description = "Retrieve a specific transaction by its ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Transaction retrieved successfully",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Unauthorized: You don't own this transaction"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    public ResponseEntity<ApiResponse<TransactionDTO>> getTransactionById(
            @Parameter(description = "Transaction ID", example = "1")
            @PathVariable Long transactionId) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            TransactionDTO transaction = transactionService.getTransactionById(username, transactionId);
            return ResponseEntity.ok(ApiResponse.success("Transaction retrieved successfully", transaction));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve transaction: " + e.getMessage()));
        }
    }

    @PutMapping("/{transactionId}")
    @Operation(summary = "Update transaction", description = "Update an existing transaction with complete data")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Transaction updated successfully",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Unauthorized: You don't own this transaction"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    public ResponseEntity<ApiResponse<TransactionDTO>> updateTransaction(
            @Parameter(description = "Transaction ID", example = "1")
            @PathVariable Long transactionId,
            @Valid @RequestBody TransactionUpdateRequest request) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            TransactionDTO transaction = transactionService.updateTransaction(username, transactionId, request);
            return ResponseEntity.ok(ApiResponse.success("Transaction updated successfully", transaction));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to update transaction: " + e.getMessage()));
        }
    }

    @PatchMapping("/{transactionId}")
    @Operation(summary = "Partially update transaction", description = "Update specific fields of an existing transaction")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Transaction partially updated successfully",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Unauthorized: You don't own this transaction"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    public ResponseEntity<ApiResponse<TransactionDTO>> patchTransaction(
            @Parameter(description = "Transaction ID", example = "1")
            @PathVariable Long transactionId,
            @Valid @RequestBody TransactionUpdateRequest request) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            TransactionDTO transaction = transactionService.patchTransaction(username, transactionId, request);
            return ResponseEntity.ok(ApiResponse.success("Transaction partially updated successfully", transaction));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to partially update transaction: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{transactionId}")
    @Operation(summary = "Delete transaction", description = "Delete a specific transaction by its ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Transaction deleted successfully",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Unauthorized: You don't own this transaction"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    public ResponseEntity<ApiResponse<String>> deleteTransaction(
            @Parameter(description = "Transaction ID", example = "1")
            @PathVariable Long transactionId) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            transactionService.deleteTransaction(username, transactionId);
            return ResponseEntity.ok(ApiResponse.success("Transaction deleted successfully", "OK"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to delete transaction: " + e.getMessage()));
        }
    }

    @DeleteMapping("/account/{accountId}")
    @Operation(summary = "Delete all transactions for account", description = "Delete all transactions associated with a specific account")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Account transactions deleted successfully",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Unauthorized: You don't own this account"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Account not found")
    })
    public ResponseEntity<ApiResponse<String>> deleteTransactionsByAccount(
            @Parameter(description = "Account ID", example = "1")
            @PathVariable Long accountId) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            int deleted = transactionService.deleteTransactionsByAccount(username, accountId);
            return ResponseEntity.ok(ApiResponse.success("Account transactions deleted successfully", String.valueOf(deleted)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to delete account transactions: " + e.getMessage()));
        }
    }
} 