package com.jarom.funbankapp.controller;

import com.jarom.funbankapp.dto.ApiResponse;
import com.jarom.funbankapp.model.Account;
import com.jarom.funbankapp.model.Budget;
import com.jarom.funbankapp.model.Transaction;
import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.AccountRepository;
import com.jarom.funbankapp.repository.BudgetRepository;
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
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;

    public DashboardController(UserRepository userRepository, AccountRepository accountRepository, 
                             TransactionRepository transactionRepository, BudgetRepository budgetRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.budgetRepository = budgetRepository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboard(Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Account> accounts = accountRepository.findByUserId(user.getId());
            List<Transaction> recentTransactions = transactionRepository.getRecentTransactions(user.getId(), 10);
            List<Budget> budgets = budgetRepository.findByUserId(user.getId());

            BigDecimal totalBalance = accounts.stream()
                    .map(Account::getBalance)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<String, Object> dashboard = new HashMap<>();
            dashboard.put("totalBalance", totalBalance);
            dashboard.put("accounts", accounts);
            dashboard.put("recentTransactions", recentTransactions);
            dashboard.put("budgets", budgets);

            return ResponseEntity.ok(ApiResponse.success("Dashboard data retrieved successfully", dashboard));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve dashboard data: " + e.getMessage()));
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardSummary(Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Account> accounts = accountRepository.findByUserId(user.getId());
            List<Transaction> recentTransactions = transactionRepository.getRecentTransactions(user.getId(), 5);

            BigDecimal totalBalance = accounts.stream()
                    .map(Account::getBalance)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<String, Object> summary = new HashMap<>();
            summary.put("totalBalance", totalBalance);
            summary.put("accountCount", accounts.size());
            summary.put("recentTransactions", recentTransactions);

            return ResponseEntity.ok(ApiResponse.success("Dashboard summary retrieved successfully", summary));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve dashboard summary: " + e.getMessage()));
        }
    }
} 