package com.jarom.funbankapp.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.jarom.funbankapp.dto.FinancialReportDTO;
import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.TransactionRepository;
import com.jarom.funbankapp.repository.UserRepository;

/**
 * Implementation of FinancialAnalysisService
 * Handles business logic for financial analysis operations
 */
@Service
public class FinancialAnalysisServiceImpl implements FinancialAnalysisService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public FinancialAnalysisServiceImpl(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    @Override
    public FinancialReportDTO generateFinancialReport(String username, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        int days = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
        
        // Get income and expense data
        Map<String, BigDecimal> incomeByCategory = transactionRepository.getIncomeByCategory(user.getId(), days);
        Map<String, BigDecimal> expensesByCategory = transactionRepository.getSpendingByCategory(user.getId(), days);
        Map<String, BigDecimal> spendingByMonth = transactionRepository.getSpendingByMonth(user.getId(), 12);
        
        // Calculate totals
        BigDecimal totalIncome = incomeByCategory.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalExpenses = expensesByCategory.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal netIncome = totalIncome.subtract(totalExpenses);
        
        // Get transaction counts
        Long totalTransactions = transactionRepository.getTransactionCount(user.getId(), days);
        Long incomeTransactions = transactionRepository.getTransactionCountByType(user.getId(), "deposit", days);
        Long expenseTransactions = transactionRepository.getTransactionCountByType(user.getId(), "withdraw", days);
        
        // Calculate averages
        BigDecimal averageDailySpending = days > 0 ? totalExpenses.divide(BigDecimal.valueOf(days), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        BigDecimal averageMonthlyIncome = totalIncome.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
        BigDecimal averageMonthlyExpenses = totalExpenses.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
        
        // Create and populate the report
        FinancialReportDTO report = new FinancialReportDTO(startDate, endDate, totalIncome, totalExpenses, netIncome);
        report.setIncomeByCategory(incomeByCategory);
        report.setExpensesByCategory(expensesByCategory);
        report.setSpendingByMonth(spendingByMonth);
        report.setAverageDailySpending(averageDailySpending);
        report.setAverageMonthlyIncome(averageMonthlyIncome);
        report.setAverageMonthlyExpenses(averageMonthlyExpenses);
        report.setTotalTransactions(totalTransactions);
        report.setIncomeTransactions(incomeTransactions);
        report.setExpenseTransactions(expenseTransactions);
        
        return report;
    }

    @Override
    public Map<String, BigDecimal> getIncomeByCategory(String username, int days) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return transactionRepository.getIncomeByCategory(user.getId(), days);
    }

    @Override
    public Map<String, BigDecimal> getSpendingByMonth(String username, int months) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return transactionRepository.getSpendingByMonth(user.getId(), months);
    }

    @Override
    public Map<String, Object> getTransactionStatistics(String username, int days) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Map<String, Object> statistics = new HashMap<>();
        
        // Get transaction counts
        Long totalTransactions = transactionRepository.getTransactionCount(user.getId(), days);
        Long incomeTransactions = transactionRepository.getTransactionCountByType(user.getId(), "deposit", days);
        Long expenseTransactions = transactionRepository.getTransactionCountByType(user.getId(), "withdraw", days);
        
        // Get spending data
        Map<String, BigDecimal> spendingByCategory = transactionRepository.getSpendingByCategory(user.getId(), days);
        Map<String, BigDecimal> incomeByCategory = transactionRepository.getIncomeByCategory(user.getId(), days);
        
        // Calculate totals
        BigDecimal totalSpending = spendingByCategory.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalIncome = incomeByCategory.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calculate averages
        BigDecimal averageTransactionAmount = totalTransactions > 0 ? 
            totalSpending.add(totalIncome).divide(BigDecimal.valueOf(totalTransactions), 2, RoundingMode.HALF_UP) : 
            BigDecimal.ZERO;
        
        BigDecimal averageDailySpending = days > 0 ? 
            totalSpending.divide(BigDecimal.valueOf(days), 2, RoundingMode.HALF_UP) : 
            BigDecimal.ZERO;
        
        // Populate statistics
        statistics.put("totalTransactions", totalTransactions);
        statistics.put("incomeTransactions", incomeTransactions);
        statistics.put("expenseTransactions", expenseTransactions);
        statistics.put("totalSpending", totalSpending);
        statistics.put("totalIncome", totalIncome);
        statistics.put("netIncome", totalIncome.subtract(totalSpending));
        statistics.put("averageTransactionAmount", averageTransactionAmount);
        statistics.put("averageDailySpending", averageDailySpending);
        statistics.put("spendingByCategory", spendingByCategory);
        statistics.put("incomeByCategory", incomeByCategory);
        statistics.put("days", days);
        
        return statistics;
    }

    @Override
    public Map<String, Object> getFinancialTrends(String username, int months) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Map<String, Object> trends = new HashMap<>();
        
        // Get monthly spending data
        Map<String, BigDecimal> spendingByMonth = transactionRepository.getSpendingByMonth(user.getId(), months);
        
        // Calculate trend indicators
        BigDecimal totalSpending = spendingByMonth.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal averageMonthlySpending = spendingByMonth.size() > 0 ? 
            totalSpending.divide(BigDecimal.valueOf(spendingByMonth.size()), 2, RoundingMode.HALF_UP) : 
            BigDecimal.ZERO;
        
        // Find highest and lowest spending months
        BigDecimal maxSpending = spendingByMonth.values().stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        BigDecimal minSpending = spendingByMonth.values().stream().min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        
        // Calculate spending volatility (standard deviation approximation)
        BigDecimal variance = spendingByMonth.values().stream()
                .map(amount -> amount.subtract(averageMonthlySpending).pow(2))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal volatility = spendingByMonth.size() > 0 ? 
            BigDecimal.valueOf(Math.sqrt(variance.divide(BigDecimal.valueOf(spendingByMonth.size()), 10, RoundingMode.HALF_UP).doubleValue())) : 
            BigDecimal.ZERO;
        
        // Populate trends
        trends.put("spendingByMonth", spendingByMonth);
        trends.put("totalSpending", totalSpending);
        trends.put("averageMonthlySpending", averageMonthlySpending);
        trends.put("maxSpending", maxSpending);
        trends.put("minSpending", minSpending);
        trends.put("spendingVolatility", volatility);
        trends.put("months", months);
        trends.put("trendDirection", getTrendDirection(spendingByMonth));
        
        return trends;
    }
    
    /**
     * Determine the overall trend direction based on monthly spending
     * @param spendingByMonth monthly spending data
     * @return trend direction string
     */
    private String getTrendDirection(Map<String, BigDecimal> spendingByMonth) {
        if (spendingByMonth.size() < 2) {
            return "insufficient_data";
        }
        
        // Convert to sorted list and compare first and last values
        BigDecimal[] values = spendingByMonth.values().toArray(new BigDecimal[0]);
        BigDecimal firstValue = values[0];
        BigDecimal lastValue = values[values.length - 1];
        
        if (lastValue.compareTo(firstValue) > 0) {
            return "increasing";
        } else if (lastValue.compareTo(firstValue) < 0) {
            return "decreasing";
        } else {
            return "stable";
        }
    }
} 