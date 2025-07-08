package com.jarom.funbankapp.service;

import com.jarom.funbankapp.dto.ExportRequest;
import java.io.File;

/**
 * Service for CSV export operations
 */
public interface CSVExportService {
    
    /**
     * Export data to CSV format
     */
    File exportData(Long userId, ExportRequest request, String filePath);
    
    /**
     * Export transactions to CSV
     */
    File exportTransactions(Long userId, ExportRequest request, String filePath);
    
    /**
     * Export debts to CSV
     */
    File exportDebts(Long userId, ExportRequest request, String filePath);
    
    /**
     * Export budgets to CSV
     */
    File exportBudgets(Long userId, ExportRequest request, String filePath);
    
    /**
     * Export goals to CSV
     */
    File exportGoals(Long userId, ExportRequest request, String filePath);
    
    /**
     * Export all financial data to CSV
     */
    File exportAllData(Long userId, ExportRequest request, String filePath);
} 