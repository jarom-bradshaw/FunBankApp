package com.jarom.funbankapp.service;

import com.jarom.funbankapp.dto.ExportRequest;
import java.io.File;

/**
 * Service for Excel export operations
 */
public interface ExcelExportService {
    
    /**
     * Export data to Excel format
     */
    File exportData(Long userId, ExportRequest request, String filePath);
    
    /**
     * Export transactions to Excel
     */
    File exportTransactions(Long userId, ExportRequest request, String filePath);
    
    /**
     * Export debts to Excel
     */
    File exportDebts(Long userId, ExportRequest request, String filePath);
    
    /**
     * Export budgets to Excel
     */
    File exportBudgets(Long userId, ExportRequest request, String filePath);
    
    /**
     * Export goals to Excel
     */
    File exportGoals(Long userId, ExportRequest request, String filePath);
    
    /**
     * Export all financial data to Excel
     */
    File exportAllData(Long userId, ExportRequest request, String filePath);
} 