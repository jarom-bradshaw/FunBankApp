package com.jarom.funbankapp.service;

import com.jarom.funbankapp.model.Account;
import com.jarom.funbankapp.model.Budget;
import com.jarom.funbankapp.model.Transaction;
import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.AccountRepository;
import com.jarom.funbankapp.repository.BudgetRepository;
import com.jarom.funbankapp.repository.TransactionRepository;
import com.jarom.funbankapp.repository.UserRepository;
import com.jarom.funbankapp.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;

    public DashboardService(UserRepository userRepository, AccountRepository accountRepository, 
                          TransactionRepository transactionRepository, BudgetRepository budgetRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.budgetRepository = budgetRepository;
    }

    /**
     * Get comprehensive financial dashboard data
     */
    public DashboardData getDashboard() {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        // Get all user data
        List<Account> accounts = accountRepository.findByUserId(user.getId());
        List<Transaction> recentTransactions = transactionRepository.getRecentTransactions(user.getId(), 10);
        List<Budget> budgets = budgetRepository.findByUserId(user.getId());
        Map<String, BigDecimal> spendingByCategory = transactionRepository.getSpendingByCategory(user.getId(), 30);
        
        // Calculate totals
        BigDecimal totalBalance = accounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalBudget = budgets.stream()
                .map(Budget::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalSpent = budgets.stream()
                .map(Budget::getSpent)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return new DashboardData(accounts, recentTransactions, budgets, spendingByCategory, 
                               totalBalance, totalBudget, totalSpent);
    }

    /**
     * Get account summary
     */
    public AccountSummary getAccountSummary() {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        List<Account> accounts = accountRepository.findByUserId(user.getId());
        BigDecimal totalBalance = accounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return new AccountSummary(accounts, totalBalance);
    }

    /**
     * Get recent transactions
     */
    public List<Transaction> getRecentTransactions(int limit) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        // Validate limit
        if (limit <= 0 || limit > 100) {
            limit = 10; // Default limit
        }
        
        return transactionRepository.getRecentTransactions(user.getId(), limit);
    }

    /**
     * Get spending analysis
     */
    public Map<String, BigDecimal> getSpendingAnalysis(int days) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        // Validate days parameter
        if (days <= 0 || days > 365) {
            days = 30; // Default to 30 days
        }
        
        return transactionRepository.getSpendingByCategory(user.getId(), days);
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    // Inner classes for response data

    public static class DashboardData {
        private final List<Account> accounts;
        private final List<Transaction> recentTransactions;
        private final List<Budget> budgets;
        private final Map<String, BigDecimal> spendingByCategory;
        private final BigDecimal totalBalance;
        private final BigDecimal totalBudget;
        private final BigDecimal totalSpent;

        public DashboardData(List<Account> accounts, List<Transaction> recentTransactions, 
                           List<Budget> budgets, Map<String, BigDecimal> spendingByCategory,
                           BigDecimal totalBalance, BigDecimal totalBudget, BigDecimal totalSpent) {
            this.accounts = accounts;
            this.recentTransactions = recentTransactions;
            this.budgets = budgets;
            this.spendingByCategory = spendingByCategory;
            this.totalBalance = totalBalance;
            this.totalBudget = totalBudget;
            this.totalSpent = totalSpent;
        }

        // Getters
        public List<Account> getAccounts() { return accounts; }
        public List<Transaction> getRecentTransactions() { return recentTransactions; }
        public List<Budget> getBudgets() { return budgets; }
        public Map<String, BigDecimal> getSpendingByCategory() { return spendingByCategory; }
        public BigDecimal getTotalBalance() { return totalBalance; }
        public BigDecimal getTotalBudget() { return totalBudget; }
        public BigDecimal getTotalSpent() { return totalSpent; }
    }

    public static class AccountSummary {
        private final List<Account> accounts;
        private final BigDecimal totalBalance;

        public AccountSummary(List<Account> accounts, BigDecimal totalBalance) {
            this.accounts = accounts;
            this.totalBalance = totalBalance;
        }

        // Getters
        public List<Account> getAccounts() { return accounts; }
        public BigDecimal getTotalBalance() { return totalBalance; }
    }
} 