package com.jarom.funbankapp.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jarom.funbankapp.dto.DepositRequest;
import com.jarom.funbankapp.dto.TransferRequest;
import com.jarom.funbankapp.dto.WithdrawRequest;
import com.jarom.funbankapp.model.Account;
import com.jarom.funbankapp.service.AccountService;
import com.jarom.funbankapp.service.FinancialAnalysisService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/accounts")
@Tag(name = "Accounts", description = "Account management and transactions")
@SecurityRequirement(name = "bearerAuth")
public class AccountController {

    private final AccountService accountService;
    private final FinancialAnalysisService financialAnalysisService;

    public AccountController(AccountService accountService, FinancialAnalysisService financialAnalysisService) {
        this.accountService = accountService;
        this.financialAnalysisService = financialAnalysisService;
    }

    // Create a new account
    @PostMapping
    @Operation(
        summary = "Create a new account", 
        description = "Creates a new account for the authenticated user. The account will be associated with the current user's ID."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "Account created successfully",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid account data provided"),
        @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<com.jarom.funbankapp.dto.ApiResponse<String>> createAccount(
        @Parameter(description = "Account details to create", required = true)
        @Valid @RequestBody com.jarom.funbankapp.dto.AccountDTO request
    ) {
        // Business logic moved to service layer
        accountService.createAccount(request);
        return ResponseEntity.ok(com.jarom.funbankapp.dto.ApiResponse.success("Account created successfully", null));
    }

    // Get all accounts for the logged-in user
    @GetMapping
    @Operation(
        summary = "Get user accounts", 
        description = "Retrieves all accounts for the authenticated user. Returns a list of account objects with balances and details."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "Accounts retrieved successfully",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<com.jarom.funbankapp.dto.ApiResponse<List<Account>>> getUserAccounts() {
        // Business logic moved to service layer
        List<Account> accounts = accountService.getUserAccounts().stream()
                .map(this::convertToModel)
                .toList();
        return ResponseEntity.ok(com.jarom.funbankapp.dto.ApiResponse.success("Accounts retrieved successfully", accounts));
    }

    // Deposit endpoint
    @PostMapping("/deposit")
    @Operation(
        summary = "Deposit funds", 
        description = "Deposits funds into a user-owned account. The amount must be positive and the account must belong to the authenticated user."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "Deposit successful",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid amount or account data"),
        @ApiResponse(responseCode = "403", description = "Unauthorized: You don't own this account"),
        @ApiResponse(responseCode = "404", description = "Account not found")
    })
    public ResponseEntity<com.jarom.funbankapp.dto.ApiResponse<String>> deposit(
        @Parameter(description = "Deposit details including account ID and amount", required = true)
        @RequestBody DepositRequest request
    ) {
        // Business logic moved to service layer
        BigDecimal newBalance = accountService.deposit(request);
        return ResponseEntity.ok(com.jarom.funbankapp.dto.ApiResponse.success("Deposit successful. New balance: $" + newBalance, null));
    }

    // Withdraw endpoint
    @PostMapping("/withdraw")
    @Operation(
        summary = "Withdraw funds", 
        description = "Withdraws funds from a user-owned account. The amount must be positive and not exceed the account balance."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "Withdrawal successful",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "Insufficient funds or invalid amount"),
        @ApiResponse(responseCode = "403", description = "Unauthorized: You don't own this account"),
        @ApiResponse(responseCode = "404", description = "Account not found")
    })
    public ResponseEntity<com.jarom.funbankapp.dto.ApiResponse<String>> withdraw(
        @Parameter(description = "Withdrawal details including account ID and amount", required = true)
        @RequestBody WithdrawRequest request
    ) {
        // Business logic moved to service layer
        BigDecimal newBalance = accountService.withdraw(request);
        return ResponseEntity.ok(com.jarom.funbankapp.dto.ApiResponse.success("Withdrawal successful. New balance: $" + newBalance, null));
    }

    // Transfer endpoint
    @PostMapping("/transfer")
    @Operation(
        summary = "Transfer funds", 
        description = "Transfers funds between user accounts. Both accounts must belong to the authenticated user."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "Transfer successful",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "Insufficient funds or invalid transfer data"),
        @ApiResponse(responseCode = "403", description = "Unauthorized: You don't own the source account"),
        @ApiResponse(responseCode = "404", description = "One or both accounts not found")
    })
    public ResponseEntity<com.jarom.funbankapp.dto.ApiResponse<String>> transfer(
        @Parameter(description = "Transfer details including source, destination, and amount", required = true)
        @RequestBody TransferRequest request
    ) {
        // Business logic moved to service layer
        accountService.transfer(request);
        return ResponseEntity.ok(com.jarom.funbankapp.dto.ApiResponse.success("Transfer successful", null));
    }

    // Financial analysis endpoint (placeholder)
    @PostMapping("/analyze")
    @Operation(
        summary = "Analyze financial data", 
        description = "Performs financial analysis using the Ollama API. This is a placeholder endpoint for future AI-powered financial insights."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "Analysis result returned",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Analysis service unavailable")
    })
    public ResponseEntity<com.jarom.funbankapp.dto.ApiResponse<String>> analyzeFinancialData(
        @Parameter(description = "Financial data to analyze", required = true)
        @RequestBody String inputData
    ) {
        // Business logic moved to service layer
        String result = financialAnalysisService.analyzeWithOllama(inputData);
        return ResponseEntity.ok(com.jarom.funbankapp.dto.ApiResponse.success("Analysis completed", result));
    }

    // Helper method to get username from JWT
    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    // Helper methods for conversion (these could be moved to a separate converter class)
    private com.jarom.funbankapp.dto.AccountDTO convertToDTO(Account account) {
        com.jarom.funbankapp.dto.AccountDTO dto = new com.jarom.funbankapp.dto.AccountDTO();
        dto.setId(account.getId());
        dto.setName(account.getName());
        dto.setAccountNumber(account.getAccountNumber());
        dto.setBalance(account.getBalance());
        dto.setAccountType(com.jarom.funbankapp.dto.AccountDTO.AccountType.valueOf(account.getAccountType()));
        dto.setColor(account.getColor());
        return dto;
    }

    private Account convertToModel(com.jarom.funbankapp.dto.AccountDTO dto) {
        Account account = new Account();
        account.setId(dto.getId());
        account.setName(dto.getName());
        account.setAccountNumber(dto.getAccountNumber());
        account.setBalance(dto.getBalance());
        account.setAccountType(dto.getAccountType().name());
        account.setColor(dto.getColor());
        return account;
    }
}
