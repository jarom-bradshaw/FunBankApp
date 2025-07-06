package com.jarom.funbankapp.service;

import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.UserRepository;
import com.jarom.funbankapp.repository.TransactionRepository;
import com.jarom.funbankapp.repository.AccountRepository;
import com.jarom.funbankapp.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public AnalyticsService(UserRepository userRepository, TransactionRepository transactionRepository, 
                          AccountRepository accountRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    /**
     * Get comprehensive financial analytics
     */
    public AnalyticsData getAnalytics() {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        // Get spending by category for different time periods
        Map<String, BigDecimal> spending30Days = transactionRepository.getSpendingByCategory(user.getId(), 30);
        Map<String, BigDecimal> spending90Days = transactionRepository.getSpendingByCategory(user.getId(), 90);
        Map<String, BigDecimal> spending365Days = transactionRepository.getSpendingByCategory(user.getId(), 365);
        
        // Calculate totals
        BigDecimal totalSpending30Days = spending30Days.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalSpending90Days = spending90Days.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalSpending365Days = spending365Days.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return new AnalyticsData(spending30Days, spending90Days, spending365Days,
                               totalSpending30Days, totalSpending90Days, totalSpending365Days);
    }

    /**
     * Get spending trends
     */
    public SpendingTrends getSpendingTrends() {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        // Get spending data for trend analysis
        Map<String, BigDecimal> currentMonth = transactionRepository.getSpendingByCategory(user.getId(), 30);
        Map<String, BigDecimal> previousMonth = transactionRepository.getSpendingByCategory(user.getId(), 60);
        
        // Calculate trends
        Map<String, BigDecimal> trends = new HashMap<>();
        for (String category : currentMonth.keySet()) {
            BigDecimal current = currentMonth.get(category);
            BigDecimal previous = previousMonth.getOrDefault(category, BigDecimal.ZERO);
            
            if (previous.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal trend = current.subtract(previous).divide(previous, 4, BigDecimal.ROUND_HALF_UP)
                        .multiply(new BigDecimal("100"));
                trends.put(category, trend);
            } else {
                trends.put(category, BigDecimal.ZERO);
            }
        }
        
        return new SpendingTrends(currentMonth, previousMonth, trends);
    }

    /**
     * Get financial summary
     */
    public FinancialSummary getFinancialSummary() {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        // Get account balances
        List<com.jarom.funbankapp.model.Account> accounts = accountRepository.findByUserId(user.getId());
        BigDecimal totalBalance = accounts.stream()
                .map(com.jarom.funbankapp.model.Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Get spending data
        Map<String, BigDecimal> spendingByCategory = transactionRepository.getSpendingByCategory(user.getId(), 30);
        BigDecimal totalSpending = spendingByCategory.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calculate savings rate
        BigDecimal savingsRate = totalBalance.compareTo(BigDecimal.ZERO) > 0 && totalSpending.compareTo(BigDecimal.ZERO) > 0
            ? totalBalance.divide(totalSpending.add(totalBalance), 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal("100"))
            : BigDecimal.ZERO;
        
        return new FinancialSummary(totalBalance, totalSpending, savingsRate, spendingByCategory);
    }

    /**
     * Get category analysis
     */
    public CategoryAnalysis getCategoryAnalysis(int days) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        // Validate days parameter
        if (days <= 0 || days > 365) {
            days = 30; // Default to 30 days
        }
        
        Map<String, BigDecimal> spendingByCategory = transactionRepository.getSpendingByCategory(user.getId(), days);
        BigDecimal totalSpending = spendingByCategory.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calculate percentages
        Map<String, BigDecimal> percentages = new HashMap<>();
        for (Map.Entry<String, BigDecimal> entry : spendingByCategory.entrySet()) {
            if (totalSpending.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal percentage = entry.getValue().divide(totalSpending, 4, BigDecimal.ROUND_HALF_UP)
                        .multiply(new BigDecimal("100"));
                percentages.put(entry.getKey(), percentage);
            } else {
                percentages.put(entry.getKey(), BigDecimal.ZERO);
            }
        }
        
        return new CategoryAnalysis(spendingByCategory, percentages, totalSpending);
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    // Inner classes for response data

    public static class AnalyticsData {
        private final Map<String, BigDecimal> spending30Days;
        private final Map<String, BigDecimal> spending90Days;
        private final Map<String, BigDecimal> spending365Days;
        private final BigDecimal totalSpending30Days;
        private final BigDecimal totalSpending90Days;
        private final BigDecimal totalSpending365Days;

        public AnalyticsData(Map<String, BigDecimal> spending30Days, Map<String, BigDecimal> spending90Days,
                           Map<String, BigDecimal> spending365Days, BigDecimal totalSpending30Days,
                           BigDecimal totalSpending90Days, BigDecimal totalSpending365Days) {
            this.spending30Days = spending30Days;
            this.spending90Days = spending90Days;
            this.spending365Days = spending365Days;
            this.totalSpending30Days = totalSpending30Days;
            this.totalSpending90Days = totalSpending90Days;
            this.totalSpending365Days = totalSpending365Days;
        }

        // Getters
        public Map<String, BigDecimal> getSpending30Days() { return spending30Days; }
        public Map<String, BigDecimal> getSpending90Days() { return spending90Days; }
        public Map<String, BigDecimal> getSpending365Days() { return spending365Days; }
        public BigDecimal getTotalSpending30Days() { return totalSpending30Days; }
        public BigDecimal getTotalSpending90Days() { return totalSpending90Days; }
        public BigDecimal getTotalSpending365Days() { return totalSpending365Days; }
    }

    public static class SpendingTrends {
        private final Map<String, BigDecimal> currentMonth;
        private final Map<String, BigDecimal> previousMonth;
        private final Map<String, BigDecimal> trends;

        public SpendingTrends(Map<String, BigDecimal> currentMonth, Map<String, BigDecimal> previousMonth,
                            Map<String, BigDecimal> trends) {
            this.currentMonth = currentMonth;
            this.previousMonth = previousMonth;
            this.trends = trends;
        }

        // Getters
        public Map<String, BigDecimal> getCurrentMonth() { return currentMonth; }
        public Map<String, BigDecimal> getPreviousMonth() { return previousMonth; }
        public Map<String, BigDecimal> getTrends() { return trends; }
    }

    public static class FinancialSummary {
        private final BigDecimal totalBalance;
        private final BigDecimal totalSpending;
        private final BigDecimal savingsRate;
        private final Map<String, BigDecimal> spendingByCategory;

        public FinancialSummary(BigDecimal totalBalance, BigDecimal totalSpending, BigDecimal savingsRate,
                              Map<String, BigDecimal> spendingByCategory) {
            this.totalBalance = totalBalance;
            this.totalSpending = totalSpending;
            this.savingsRate = savingsRate;
            this.spendingByCategory = spendingByCategory;
        }

        // Getters
        public BigDecimal getTotalBalance() { return totalBalance; }
        public BigDecimal getTotalSpending() { return totalSpending; }
        public BigDecimal getSavingsRate() { return savingsRate; }
        public Map<String, BigDecimal> getSpendingByCategory() { return spendingByCategory; }
    }

    public static class CategoryAnalysis {
        private final Map<String, BigDecimal> spendingByCategory;
        private final Map<String, BigDecimal> percentages;
        private final BigDecimal totalSpending;

        public CategoryAnalysis(Map<String, BigDecimal> spendingByCategory, Map<String, BigDecimal> percentages,
                              BigDecimal totalSpending) {
            this.spendingByCategory = spendingByCategory;
            this.percentages = percentages;
            this.totalSpending = totalSpending;
        }

        // Getters
        public Map<String, BigDecimal> getSpendingByCategory() { return spendingByCategory; }
        public Map<String, BigDecimal> getPercentages() { return percentages; }
        public BigDecimal getTotalSpending() { return totalSpending; }
    }
} 