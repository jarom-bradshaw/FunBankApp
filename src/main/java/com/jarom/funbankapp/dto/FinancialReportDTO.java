package com.jarom.funbankapp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * Data Transfer Object for financial reports
 * Used for API responses to avoid infinite recursion
 */
public class FinancialReportDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal netIncome;
    private Map<String, BigDecimal> incomeByCategory;
    private Map<String, BigDecimal> expensesByCategory;
    private Map<String, BigDecimal> spendingByMonth;
    private BigDecimal averageDailySpending;
    private BigDecimal averageMonthlyIncome;
    private BigDecimal averageMonthlyExpenses;
    private Long totalTransactions;
    private Long incomeTransactions;
    private Long expenseTransactions;

    // Constructors
    public FinancialReportDTO() {}

    public FinancialReportDTO(LocalDate startDate, LocalDate endDate, BigDecimal totalIncome, 
                             BigDecimal totalExpenses, BigDecimal netIncome) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalIncome = totalIncome;
        this.totalExpenses = totalExpenses;
        this.netIncome = netIncome;
    }

    // Getters and setters
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public BigDecimal getTotalIncome() { return totalIncome; }
    public void setTotalIncome(BigDecimal totalIncome) { this.totalIncome = totalIncome; }

    public BigDecimal getTotalExpenses() { return totalExpenses; }
    public void setTotalExpenses(BigDecimal totalExpenses) { this.totalExpenses = totalExpenses; }

    public BigDecimal getNetIncome() { return netIncome; }
    public void setNetIncome(BigDecimal netIncome) { this.netIncome = netIncome; }

    public Map<String, BigDecimal> getIncomeByCategory() { return incomeByCategory; }
    public void setIncomeByCategory(Map<String, BigDecimal> incomeByCategory) { this.incomeByCategory = incomeByCategory; }

    public Map<String, BigDecimal> getExpensesByCategory() { return expensesByCategory; }
    public void setExpensesByCategory(Map<String, BigDecimal> expensesByCategory) { this.expensesByCategory = expensesByCategory; }

    public Map<String, BigDecimal> getSpendingByMonth() { return spendingByMonth; }
    public void setSpendingByMonth(Map<String, BigDecimal> spendingByMonth) { this.spendingByMonth = spendingByMonth; }

    public BigDecimal getAverageDailySpending() { return averageDailySpending; }
    public void setAverageDailySpending(BigDecimal averageDailySpending) { this.averageDailySpending = averageDailySpending; }

    public BigDecimal getAverageMonthlyIncome() { return averageMonthlyIncome; }
    public void setAverageMonthlyIncome(BigDecimal averageMonthlyIncome) { this.averageMonthlyIncome = averageMonthlyIncome; }

    public BigDecimal getAverageMonthlyExpenses() { return averageMonthlyExpenses; }
    public void setAverageMonthlyExpenses(BigDecimal averageMonthlyExpenses) { this.averageMonthlyExpenses = averageMonthlyExpenses; }

    public Long getTotalTransactions() { return totalTransactions; }
    public void setTotalTransactions(Long totalTransactions) { this.totalTransactions = totalTransactions; }

    public Long getIncomeTransactions() { return incomeTransactions; }
    public void setIncomeTransactions(Long incomeTransactions) { this.incomeTransactions = incomeTransactions; }

    public Long getExpenseTransactions() { return expenseTransactions; }
    public void setExpenseTransactions(Long expenseTransactions) { this.expenseTransactions = expenseTransactions; }
} 