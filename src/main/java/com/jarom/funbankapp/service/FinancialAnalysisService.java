package com.jarom.funbankapp.service;

import java.time.LocalDate;
import java.util.Map;

import com.jarom.funbankapp.dto.FinancialReportDTO;

/**
 * Service layer for financial analysis operations
 * Handles business logic for financial reporting and analytics
 */
public interface FinancialAnalysisService {
    
    /**
     * Generate comprehensive financial report
     * @param username the username
     * @param startDate start date for the report
     * @param endDate end date for the report
     * @return financial report DTO
     */
    FinancialReportDTO generateFinancialReport(String username, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get income breakdown by category
     * @param username the username
     * @param days number of days to look back
     * @return map of category to total income
     */
    Map<String, java.math.BigDecimal> getIncomeByCategory(String username, int days);
    
    /**
     * Get spending breakdown by month
     * @param username the username
     * @param months number of months to look back
     * @return map of month to total spending
     */
    Map<String, java.math.BigDecimal> getSpendingByMonth(String username, int months);
    
    /**
     * Get transaction statistics
     * @param username the username
     * @param days number of days to look back
     * @return map containing transaction statistics
     */
    Map<String, Object> getTransactionStatistics(String username, int days);
    
    /**
     * Get financial trends analysis
     * @param username the username
     * @param months number of months to analyze
     * @return map containing trend analysis data
     */
    Map<String, Object> getFinancialTrends(String username, int months);
} 