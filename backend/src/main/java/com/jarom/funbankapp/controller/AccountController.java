package com.jarom.funbankapp.controller;

import com.jarom.funbankapp.model.Account;
import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.AccountDAO;
import com.jarom.funbankapp.repository.UserDAO;
import com.jarom.funbankapp.repository.TransactionDAO;
import com.jarom.funbankapp.dto.DepositRequest;
import com.jarom.funbankapp.dto.WithdrawRequest;
import com.jarom.funbankapp.dto.TransferRequest;
import com.jarom.funbankapp.service.FinancialAnalysisService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/accounts")
@io.swagger.v3.oas.annotations.tags.Tag(name = "Accounts", description = "Account management and transactions")
public class AccountController {

    private final AccountDAO accountDAO;
    private final UserDAO userDAO;
    private final TransactionDAO transactionDAO;
    private final FinancialAnalysisService financialAnalysisService;

    public AccountController(AccountDAO accountDAO, UserDAO userDAO, TransactionDAO transactionDAO, FinancialAnalysisService financialAnalysisService) {
        this.accountDAO = accountDAO;
        this.userDAO = userDAO;
        this.transactionDAO = transactionDAO;
        this.financialAnalysisService = financialAnalysisService;
    }

    // Create a new account
    @PostMapping
    @Operation(summary = "Create a new account", description = "Creates a new account for the authenticated user.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Account created successfully")
    })
    public ResponseEntity<?> createAccount(@RequestBody Account request) {
        String username = getCurrentUsername();
        User user = userDAO.findByUsername(username);

        request.setAccountNumber(UUID.randomUUID().toString());

        request.setUserId(user.getId());

        accountDAO.createAccount(request);

        return ResponseEntity.ok("Account created.");
    }

    // Get all accounts for the logged-in user
    @GetMapping
    @Operation(summary = "Get user accounts", description = "Retrieves all accounts for the authenticated user.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Accounts retrieved successfully")
    })
    public ResponseEntity<List<Account>> getUserAccounts() {
        String username = getCurrentUsername();
        User user = userDAO.findByUsername(username);

        List<Account> accounts = accountDAO.findByUserId(user.getId());
        return ResponseEntity.ok(accounts);
    }

    // Deposit endpoint
    @PostMapping("/deposit")
    @Operation(summary = "Deposit funds", description = "Deposits funds into a user-owned account.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Deposit successful"),
        @ApiResponse(responseCode = "403", description = "Unauthorized: You don't own this account.")
    })
    public ResponseEntity<?> deposit(@RequestBody DepositRequest request) {
        String username = getCurrentUsername();
        User user = userDAO.findByUsername(username);

        System.out.println("🔍 Checking account ownership");
        System.out.println("👤 User ID: " + user.getId());
        System.out.println("📥 Deposit accountId: " + request.getAccountId());

        List<Account> owned = accountDAO.findByUserId(user.getId());
        owned.forEach(acc -> System.out.println("✅ User owns: " + acc.getId()));


        if (!userOwnsAccount(user, request.getAccountId())) {
            System.out.println("👤 Logged in as: " + username);
            System.out.println("🔍 Verifying ownership of accountId: " + request.getAccountId());
            List<Account> accounts = accountDAO.findByUserId(user.getId());
            accounts.forEach(acc -> System.out.println("✅ User owns accountId: " + acc.getId()));

            return ResponseEntity.status(403).body("Unauthorized: You don't own this account.");
        }

        BigDecimal currentBalance = accountDAO.getBalance(request.getAccountId());
        BigDecimal newBalance = currentBalance.add(request.getAmount());

        accountDAO.updateBalance(request.getAccountId(), newBalance);

        transactionDAO.logTransaction(
                request.getAccountId(),
                "deposit",
                request.getAmount(),
                request.getDescription() != null ? request.getDescription() : "Deposit"
        );

        return ResponseEntity.ok("Deposit successful. New balance: $" + newBalance);
    }

    // Withdraw endpoint
    @PostMapping("/withdraw")
    @Operation(summary = "Withdraw funds", description = "Withdraws funds from a user-owned account.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Withdrawal successful"),
        @ApiResponse(responseCode = "400", description = "Insufficient funds."),
        @ApiResponse(responseCode = "403", description = "Unauthorized: You don't own this account.")
    })
    public ResponseEntity<?> withdraw(@RequestBody WithdrawRequest request) {
        String username = getCurrentUsername();
        User user = userDAO.findByUsername(username);

        if (!userOwnsAccount(user, request.getAccountId())) {
            return ResponseEntity.status(403).body("Unauthorized: You don't own this account.");
        }

        BigDecimal currentBalance = accountDAO.getBalance(request.getAccountId());

        if (request.getAmount().compareTo(currentBalance) > 0) {
            return ResponseEntity.badRequest().body("Insufficient funds.");
        }

        BigDecimal newBalance = currentBalance.subtract(request.getAmount());

        accountDAO.updateBalance(request.getAccountId(), newBalance);

        transactionDAO.logTransaction(
                request.getAccountId(),
                "withdraw",
                request.getAmount(),
                request.getDescription() != null ? request.getDescription() : "Withdrawal"
        );

        return ResponseEntity.ok("Withdrawal successful. New balance: $" + newBalance);
    }

    // Transfer endpoint
    @PostMapping("/transfer")
    @Operation(summary = "Transfer funds", description = "Transfers funds between user accounts.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transfer successful"),
        @ApiResponse(responseCode = "400", description = "Insufficient funds."),
        @ApiResponse(responseCode = "403", description = "Unauthorized: You don't own the source account.")
    })
    public ResponseEntity<?> transfer(@RequestBody TransferRequest request) {
        String username = getCurrentUsername();
        User user = userDAO.findByUsername(username);

        if (!userOwnsAccount(user, request.getFromAccountId())) {
            return ResponseEntity.status(403).body("Unauthorized: You don't own the source account.");
        }

        BigDecimal fromBalance = accountDAO.getBalance(request.getFromAccountId());

        if (request.getAmount().compareTo(fromBalance) > 0) {
            return ResponseEntity.badRequest().body("Insufficient funds.");
        }

        BigDecimal toBalance = accountDAO.getBalance(request.getToAccountId());
        BigDecimal newFromBalance = fromBalance.subtract(request.getAmount());
        BigDecimal newToBalance = toBalance.add(request.getAmount());

        accountDAO.updateBalance(request.getFromAccountId(), newFromBalance);
        accountDAO.updateBalance(request.getToAccountId(), newToBalance);

        transactionDAO.logTransaction(
                request.getFromAccountId(),
                "transfer",
                request.getAmount(),
                request.getDescription() != null ? request.getDescription() : "Transfer to account " + request.getToAccountId()
        );

        transactionDAO.logTransaction(
                request.getToAccountId(),
                "deposit",
                request.getAmount(),
                "Transfer from account " + request.getFromAccountId()
        );

        return ResponseEntity.ok("Transfer successful.");
    }

    // Financial analysis endpoint (placeholder)
    @PostMapping("/analyze")
    @Operation(summary = "Analyze financial data", description = "Performs financial analysis using the Ollama API (placeholder).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Analysis result returned")
    })
    public ResponseEntity<?> analyzeFinancialData(@RequestBody String inputData) {
        // TODO: Define input/output structure
        String result = financialAnalysisService.analyzeWithOllama(inputData);
        return ResponseEntity.ok(result);
    }

    // Reusable method to verify account ownership
    private boolean userOwnsAccount(User user, Long accountId) {
        System.out.println("🧪 Checking if user owns accountId: " + accountId);
        return accountDAO.findByUserId(user.getId())
                .stream()
                .anyMatch(acc -> acc.getId().equals(accountId));

    }

    // Helper method to get username from JWT
    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }
}
