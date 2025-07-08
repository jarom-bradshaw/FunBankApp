package com.jarom.funbankapp.service;

import com.jarom.funbankapp.dto.ExportRequest;
import java.io.File;

/**
 * Service for JSON export operations
 */
public interface JSONExportService {
    
    /**
     * Export data to JSON format
     */
    File exportData(Long userId, ExportRequest request, String filePath);
    
    /**
     * Export transactions to JSON
     */
    File exportTransactions(Long userId, ExportRequest request, String filePath);
    
    /**
     * Export debts to JSON
     */
    File exportDebts(Long userId, ExportRequest request, String filePath);
    
    /**
     * Export budgets to JSON
     */
    File exportBudgets(Long userId, ExportRequest request, String filePath);
    
    /**
     * Export goals to JSON
     */
    File exportGoals(Long userId, ExportRequest request, String filePath);
    
    /**
     * Export all financial data to JSON
     */
    File exportAllData(Long userId, ExportRequest request, String filePath);
} 