package com.jarom.funbankapp.service;

import com.jarom.funbankapp.dto.ExportRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Implementation of CSV export service
 */
@Service
public class CSVExportServiceImpl implements CSVExportService {

    private static final Logger logger = LoggerFactory.getLogger(CSVExportServiceImpl.class);

    @Override
    public File exportData(Long userId, ExportRequest request, String filePath) {
        logger.info("Exporting data to CSV for user {}: type={}", userId, request.getExportType());

        switch (request.getExportType().toLowerCase()) {
            case "transactions":
                return exportTransactions(userId, request, filePath);
            case "debts":
                return exportDebts(userId, request, filePath);
            case "budgets":
                return exportBudgets(userId, request, filePath);
            case "goals":
                return exportGoals(userId, request, filePath);
            case "all":
                return exportAllData(userId, request, filePath);
            default:
                throw new RuntimeException("Unsupported export type: " + request.getExportType());
        }
    }

    @Override
    public File exportTransactions(Long userId, ExportRequest request, String filePath) {
        logger.info("Exporting transactions to CSV for user {}", userId);

        try (FileWriter writer = new FileWriter(filePath)) {
            // Write CSV header
            writer.write("Date,Description,Amount,Category,Account,Type\n");

            // For now, write sample data
            // In a real implementation, this would query the database
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            writer.write(String.format("%s,Sample Transaction,100.00,Groceries,Checking,Expense\n", timestamp));
            writer.write(String.format("%s,Salary Deposit,2500.00,Income,Checking,Income\n", timestamp));

            logger.info("Transactions exported successfully to: {}", filePath);
            return new File(filePath);

        } catch (IOException e) {
            logger.error("Failed to export transactions to CSV", e);
            throw new RuntimeException("Failed to export transactions", e);
        }
    }

    @Override
    public File exportDebts(Long userId, ExportRequest request, String filePath) {
        logger.info("Exporting debts to CSV for user {}", userId);

        try (FileWriter writer = new FileWriter(filePath)) {
            // Write CSV header
            writer.write("Debt Name,Original Amount,Current Balance,Interest Rate,Minimum Payment,Due Date,Type\n");

            // For now, write sample data
            // In a real implementation, this would query the debt management system
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            writer.write(String.format("Credit Card 1,5000.00,3200.00,18.99,150.00,%s,Credit Card\n", timestamp));
            writer.write(String.format("Student Loan,25000.00,22000.00,5.50,200.00,%s,Student Loan\n", timestamp));

            logger.info("Debts exported successfully to: {}", filePath);
            return new File(filePath);

        } catch (IOException e) {
            logger.error("Failed to export debts to CSV", e);
            throw new RuntimeException("Failed to export debts", e);
        }
    }

    @Override
    public File exportBudgets(Long userId, ExportRequest request, String filePath) {
        logger.info("Exporting budgets to CSV for user {}", userId);

        try (FileWriter writer = new FileWriter(filePath)) {
            // Write CSV header
            writer.write("Category,Budget Amount,Spent Amount,Remaining,Percentage Used\n");

            // For now, write sample data
            writer.write("Groceries,500.00,320.00,180.00,64.00%\n");
            writer.write("Entertainment,200.00,150.00,50.00,75.00%\n");
            writer.write("Transportation,300.00,280.00,20.00,93.33%\n");

            logger.info("Budgets exported successfully to: {}", filePath);
            return new File(filePath);

        } catch (IOException e) {
            logger.error("Failed to export budgets to CSV", e);
            throw new RuntimeException("Failed to export budgets", e);
        }
    }

    @Override
    public File exportGoals(Long userId, ExportRequest request, String filePath) {
        logger.info("Exporting goals to CSV for user {}", userId);

        try (FileWriter writer = new FileWriter(filePath)) {
            // Write CSV header
            writer.write("Goal Name,Target Amount,Current Amount,Progress,Target Date,Status\n");

            // For now, write sample data
            String timestamp = LocalDateTime.now().plusMonths(6).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            writer.write(String.format("Emergency Fund,10000.00,7500.00,75.00,%s,In Progress\n", timestamp));
            writer.write(String.format("Vacation Fund,5000.00,3000.00,60.00,%s,In Progress\n", timestamp));

            logger.info("Goals exported successfully to: {}", filePath);
            return new File(filePath);

        } catch (IOException e) {
            logger.error("Failed to export goals to CSV", e);
            throw new RuntimeException("Failed to export goals", e);
        }
    }

    @Override
    public File exportAllData(Long userId, ExportRequest request, String filePath) {
        logger.info("Exporting all financial data to CSV for user {}", userId);

        try (FileWriter writer = new FileWriter(filePath)) {
            // Write comprehensive CSV header
            writer.write("Data Type,Date,Description,Amount,Category,Account,Type,Status\n");

            // For now, write sample data from all categories
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            writer.write(String.format("Transaction,%s,Grocery Shopping,85.50,Groceries,Checking,Expense,Completed\n", timestamp));
            writer.write(String.format("Debt,%s,Credit Card Payment,150.00,Credit Card,Checking,Payment,Completed\n", timestamp));
            writer.write(String.format("Budget,%s,Monthly Budget,2000.00,Monthly,Checking,Budget,Active\n", timestamp));
            writer.write(String.format("Goal,%s,Emergency Fund,500.00,Savings,Savings,Goal,In Progress\n", timestamp));

            logger.info("All financial data exported successfully to: {}", filePath);
            return new File(filePath);

        } catch (IOException e) {
            logger.error("Failed to export all data to CSV", e);
            throw new RuntimeException("Failed to export all data", e);
        }
    }
} 