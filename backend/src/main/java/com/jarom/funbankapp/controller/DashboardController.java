package com.jarom.funbankapp.controller;

import com.jarom.funbankapp.model.Account;
import com.jarom.funbankapp.model.Budget;
import com.jarom.funbankapp.model.Transaction;
import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.AccountDAO;
import com.jarom.funbankapp.repository.BudgetDAO;
import com.jarom.funbankapp.repository.TransactionDAO;
import com.jarom.funbankapp.repository.UserDAO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/dashboard")
@io.swagger.v3.oas.annotations.tags.Tag(name = "Dashboard", description = "Financial dashboard and overview")
public class DashboardController {

    private final UserDAO userDAO;
    private final AccountDAO accountDAO;
    private final TransactionDAO transactionDAO;
    private final BudgetDAO budgetDAO;

    public DashboardController(UserDAO userDAO, AccountDAO accountDAO, TransactionDAO transactionDAO, BudgetDAO budgetDAO) {
        this.userDAO = userDAO;
        this.accountDAO = accountDAO;
        this.transactionDAO = transactionDAO;
        this.budgetDAO = budgetDAO;
    }

    @GetMapping
    @Operation(summary = "Get financial dashboard", description = "Retrieves comprehensive financial overview for the authenticated user.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Dashboard data retrieved successfully")
    })
    public ResponseEntity<?> getDashboard() {
        String username = getCurrentUsername();
        User user = userDAO.findByUsername(username);

        // Get user accounts
        List<Account> accounts = accountDAO.findByUserId(user.getId());
        
        // Calculate total balance
        BigDecimal totalBalance = accounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Get recent transactions (last 10)
        List<Transaction> recentTransactions = transactionDAO.getRecentTransactions(user.getId(), 10);

        // Get budgets
        List<Budget> budgets = budgetDAO.findByUserId(user.getId());
        
        // Calculate budget summary
        BigDecimal totalBudgeted = budgets.stream()
                .map(Budget::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Get spending by category (last 30 days)
        Map<String, BigDecimal> spendingByCategory = transactionDAO.getSpendingByCategory(user.getId(), 30);

        DashboardData dashboardData = new DashboardData(
                totalBalance,
                accounts,
                recentTransactions,
                budgets,
                totalBudgeted,
                spendingByCategory
        );

        return ResponseEntity.ok(dashboardData);
    }

    @GetMapping("/accounts")
    @Operation(summary = "Get account summary", description = "Retrieves account balances and summary.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Account summary retrieved successfully")
    })
    public ResponseEntity<?> getAccountSummary() {
        String username = getCurrentUsername();
        User user = userDAO.findByUsername(username);

        List<Account> accounts = accountDAO.findByUserId(user.getId());
        BigDecimal totalBalance = accounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ResponseEntity.ok(new AccountSummary(accounts, totalBalance));
    }

    @GetMapping("/transactions")
    @Operation(summary = "Get recent transactions", description = "Retrieves recent transactions for the user.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Recent transactions retrieved successfully")
    })
    public ResponseEntity<List<Transaction>> getRecentTransactions(@RequestParam(defaultValue = "10") int limit) {
        String username = getCurrentUsername();
        User user = userDAO.findByUsername(username);

        List<Transaction> transactions = transactionDAO.getRecentTransactions(user.getId(), limit);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/spending")
    @Operation(summary = "Get spending analysis", description = "Retrieves spending analysis by category.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Spending analysis retrieved successfully")
    })
    public ResponseEntity<Map<String, BigDecimal>> getSpendingAnalysis(@RequestParam(defaultValue = "30") int days) {
        String username = getCurrentUsername();
        User user = userDAO.findByUsername(username);

        Map<String, BigDecimal> spendingByCategory = transactionDAO.getSpendingByCategory(user.getId(), days);
        return ResponseEntity.ok(spendingByCategory);
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    // Inner classes for response data
    public static class DashboardData {
        private BigDecimal totalBalance;
        private List<Account> accounts;
        private List<Transaction> recentTransactions;
        private List<Budget> budgets;
        private BigDecimal totalBudgeted;
        private Map<String, BigDecimal> spendingByCategory;

        public DashboardData(BigDecimal totalBalance, List<Account> accounts, List<Transaction> recentTransactions,
                           List<Budget> budgets, BigDecimal totalBudgeted, Map<String, BigDecimal> spendingByCategory) {
            this.totalBalance = totalBalance;
            this.accounts = accounts;
            this.recentTransactions = recentTransactions;
            this.budgets = budgets;
            this.totalBudgeted = totalBudgeted;
            this.spendingByCategory = spendingByCategory;
        }

        // Getters
        public BigDecimal getTotalBalance() { return totalBalance; }
        public List<Account> getAccounts() { return accounts; }
        public List<Transaction> getRecentTransactions() { return recentTransactions; }
        public List<Budget> getBudgets() { return budgets; }
        public BigDecimal getTotalBudgeted() { return totalBudgeted; }
        public Map<String, BigDecimal> getSpendingByCategory() { return spendingByCategory; }
    }

    public static class AccountSummary {
        private List<Account> accounts;
        private BigDecimal totalBalance;

        public AccountSummary(List<Account> accounts, BigDecimal totalBalance) {
            this.accounts = accounts;
            this.totalBalance = totalBalance;
        }

        // Getters
        public List<Account> getAccounts() { return accounts; }
        public BigDecimal getTotalBalance() { return totalBalance; }
    }
} 