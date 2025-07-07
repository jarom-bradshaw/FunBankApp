package com.jarom.funbankapp.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jarom.funbankapp.dto.AccountUpdateRequest;
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

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // Create a new account
    @PostMapping
    @Operation(
        summary = "Create a new account", 
        description = "Creates a new account for the authenticated user. The account will be associated with the current user's ID."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Account created successfully",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid account data provided"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "User not authenticated")
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
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Accounts retrieved successfully",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "User not authenticated")
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
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Deposit successful",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid amount or account data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Unauthorized: You don't own this account"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Account not found")
    })
    public ResponseEntity<com.jarom.funbankapp.dto.ApiResponse<String>> deposit(
        @Parameter(description = "Deposit details including account ID and amount", required = true)
        @Valid @RequestBody DepositRequest request
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
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Withdrawal successful",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Insufficient funds or invalid amount"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Unauthorized: You don't own this account"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Account not found")
    })
    public ResponseEntity<com.jarom.funbankapp.dto.ApiResponse<String>> withdraw(
        @Parameter(description = "Withdrawal details including account ID and amount", required = true)
        @Valid @RequestBody WithdrawRequest request
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
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Transfer successful",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Insufficient funds or invalid transfer data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Unauthorized: You don't own the source account"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "One or both accounts not found")
    })
    public ResponseEntity<com.jarom.funbankapp.dto.ApiResponse<String>> transfer(
        @Parameter(description = "Transfer details including source, destination, and amount", required = true)
        @Valid @RequestBody TransferRequest request
    ) {
        // Business logic moved to service layer
        accountService.transfer(request);
        return ResponseEntity.ok(com.jarom.funbankapp.dto.ApiResponse.success("Transfer successful", null));
    }

    // Patch account endpoint
    @PatchMapping("/{accountId}")
    @Operation(
        summary = "Partially update account", 
        description = "Update specific fields of an existing account. Only non-critical fields can be updated."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Account partially updated successfully",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid account data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Unauthorized: You don't own this account"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Account not found")
    })
    public ResponseEntity<com.jarom.funbankapp.dto.ApiResponse<String>> patchAccount(
        @Parameter(description = "Account ID to update", example = "1")
        @PathVariable Long accountId,
        @Valid @RequestBody AccountUpdateRequest request
    ) {
        // Business logic moved to service layer
        accountService.patchAccount(accountId, request);
        return ResponseEntity.ok(com.jarom.funbankapp.dto.ApiResponse.success("Account partially updated successfully", 
            "Account with ID " + accountId + " has been updated"));
    }

    // Put account endpoint (full update)
    @PutMapping("/{accountId}")
    @Operation(
        summary = "Update account (full update)", 
        description = "Update all fields of an existing account. All fields must be provided."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Account updated successfully",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid account data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Unauthorized: You don't own this account"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Account not found")
    })
    public ResponseEntity<com.jarom.funbankapp.dto.ApiResponse<com.jarom.funbankapp.dto.AccountDTO>> putAccount(
        @Parameter(description = "Account ID to update", example = "1")
        @PathVariable Long accountId,
        @Parameter(description = "Complete account data to update", required = true)
        @Valid @RequestBody com.jarom.funbankapp.dto.AccountDTO request
    ) {
        // Business logic moved to service layer
        com.jarom.funbankapp.dto.AccountDTO updatedAccount = accountService.updateAccount(accountId, request);
        return ResponseEntity.ok(com.jarom.funbankapp.dto.ApiResponse.success("Account updated successfully", updatedAccount));
    }

    // Delete account endpoint
    @DeleteMapping("/{accountId}")
    @Operation(
        summary = "Delete account", 
        description = "Deletes an account and all its associated transactions. This action is irreversible."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Account deleted successfully",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Account has balance or invalid account data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Unauthorized: You don't own this account"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Account not found")
    })
    public ResponseEntity<com.jarom.funbankapp.dto.ApiResponse<String>> deleteAccount(
        @Parameter(description = "Account ID to delete", example = "1")
        @PathVariable Long accountId
    ) {
        // Business logic moved to service layer
        accountService.deleteAccount(accountId);
        return ResponseEntity.ok(com.jarom.funbankapp.dto.ApiResponse.success("Account deleted successfully", 
            "Account with ID " + accountId + " and all its transactions have been deleted"));
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
