package com.jarom.funbankapp.controller;

import com.jarom.funbankapp.dto.ApiResponse;
import com.jarom.funbankapp.model.Account;
import com.jarom.funbankapp.model.Transaction;
import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.AccountRepository;
import com.jarom.funbankapp.repository.TransactionRepository;
import com.jarom.funbankapp.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public AnalyticsController(UserRepository userRepository, TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @GetMapping("/spending")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getSpendingAnalytics(Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Map<String, BigDecimal> spendingByCategory = transactionRepository.getSpendingByCategory(user.getId(), 30);
            return ResponseEntity.ok(ApiResponse.success("Spending analytics retrieved successfully", spendingByCategory));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve spending analytics: " + e.getMessage()));
        }
    }

    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<List<Transaction>>> getTransactionAnalytics(Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Transaction> transactions = transactionRepository.getRecentTransactions(user.getId(), 100);
            return ResponseEntity.ok(ApiResponse.success("Transaction analytics retrieved successfully", transactions));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve transaction analytics: " + e.getMessage()));
        }
    }

    @GetMapping("/accounts")
    public ResponseEntity<ApiResponse<List<Account>>> getAccountAnalytics(Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Account> accounts = accountRepository.findByUserId(user.getId());
            return ResponseEntity.ok(ApiResponse.success("Account analytics retrieved successfully", accounts));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve account analytics: " + e.getMessage()));
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAnalyticsSummary(Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Account> accounts = accountRepository.findByUserId(user.getId());
            Map<String, BigDecimal> spendingByCategory = transactionRepository.getSpendingByCategory(user.getId(), 30);

            BigDecimal totalBalance = accounts.stream()
                    .map(Account::getBalance)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalSpending = spendingByCategory.values().stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<String, Object> summary = new HashMap<>();
            summary.put("totalBalance", totalBalance);
            summary.put("totalSpending", totalSpending);
            summary.put("spendingByCategory", spendingByCategory);
            summary.put("accountCount", accounts.size());

            return ResponseEntity.ok(ApiResponse.success("Analytics summary retrieved successfully", summary));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve analytics summary: " + e.getMessage()));
        }
    }
} 