package com.jarom.funbankapp.service;

import com.jarom.funbankapp.dto.ExportRequest;
import java.io.File;

/**
 * Service for PDF export operations
 */
public interface PDFExportService {
    
    /**
     * Export data to PDF format
     */
    File exportData(Long userId, ExportRequest request, String filePath);
    
    /**
     * Export transactions to PDF
     */
    File exportTransactions(Long userId, ExportRequest request, String filePath);
    
    /**
     * Export debts to PDF
     */
    File exportDebts(Long userId, ExportRequest request, String filePath);
    
    /**
     * Export budgets to PDF
     */
    File exportBudgets(Long userId, ExportRequest request, String filePath);
    
    /**
     * Export goals to PDF
     */
    File exportGoals(Long userId, ExportRequest request, String filePath);
    
    /**
     * Export all financial data to PDF
     */
    File exportAllData(Long userId, ExportRequest request, String filePath);
} 