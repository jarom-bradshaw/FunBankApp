package com.jarom.funbankapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jarom.funbankapp.dto.ExportRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Implementation of JSON export service
 */
@Service
public class JSONExportServiceImpl implements JSONExportService {

    private static final Logger logger = LoggerFactory.getLogger(JSONExportServiceImpl.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public File exportData(Long userId, ExportRequest request, String filePath) {
        logger.info("Exporting data to JSON for user {}: type={}", userId, request.getExportType());

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
        logger.info("Exporting transactions to JSON for user {}", userId);

        try {
            ObjectNode rootNode = objectMapper.createObjectNode();
            rootNode.put("exportType", "transactions");
            rootNode.put("userId", userId);
            rootNode.put("generatedAt", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            rootNode.put("format", "json");

            ArrayNode transactionsNode = rootNode.putArray("transactions");
            
            // Add sample transaction data
            ObjectNode transaction1 = transactionsNode.addObject();
            transaction1.put("date", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            transaction1.put("description", "Sample Transaction");
            transaction1.put("amount", 100.00);
            transaction1.put("category", "Groceries");
            transaction1.put("account", "Checking");
            transaction1.put("type", "Expense");

            ObjectNode transaction2 = transactionsNode.addObject();
            transaction2.put("date", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            transaction2.put("description", "Salary Deposit");
            transaction2.put("amount", 2500.00);
            transaction2.put("category", "Income");
            transaction2.put("account", "Checking");
            transaction2.put("type", "Income");

            // Write to file
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), rootNode);

            logger.info("Transactions exported successfully to: {}", filePath);
            return new File(filePath);

        } catch (IOException e) {
            logger.error("Failed to export transactions to JSON", e);
            throw new RuntimeException("Failed to export transactions", e);
        }
    }

    @Override
    public File exportDebts(Long userId, ExportRequest request, String filePath) {
        logger.info("Exporting debts to JSON for user {}", userId);

        try {
            ObjectNode rootNode = objectMapper.createObjectNode();
            rootNode.put("exportType", "debts");
            rootNode.put("userId", userId);
            rootNode.put("generatedAt", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            rootNode.put("format", "json");

            ArrayNode debtsNode = rootNode.putArray("debts");
            
            // Add sample debt data
            ObjectNode debt1 = debtsNode.addObject();
            debt1.put("name", "Credit Card 1");
            debt1.put("originalAmount", 5000.00);
            debt1.put("currentBalance", 3200.00);
            debt1.put("interestRate", 18.99);
            debt1.put("minimumPayment", 150.00);
            debt1.put("type", "Credit Card");

            ObjectNode debt2 = debtsNode.addObject();
            debt2.put("name", "Student Loan");
            debt2.put("originalAmount", 25000.00);
            debt2.put("currentBalance", 22000.00);
            debt2.put("interestRate", 5.50);
            debt2.put("minimumPayment", 200.00);
            debt2.put("type", "Student Loan");

            // Write to file
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), rootNode);

            logger.info("Debts exported successfully to: {}", filePath);
            return new File(filePath);

        } catch (IOException e) {
            logger.error("Failed to export debts to JSON", e);
            throw new RuntimeException("Failed to export debts", e);
        }
    }

    @Override
    public File exportBudgets(Long userId, ExportRequest request, String filePath) {
        logger.info("Exporting budgets to JSON for user {}", userId);

        try {
            ObjectNode rootNode = objectMapper.createObjectNode();
            rootNode.put("exportType", "budgets");
            rootNode.put("userId", userId);
            rootNode.put("generatedAt", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            rootNode.put("format", "json");

            ArrayNode budgetsNode = rootNode.putArray("budgets");
            
            // Add sample budget data
            ObjectNode budget1 = budgetsNode.addObject();
            budget1.put("category", "Groceries");
            budget1.put("budgetAmount", 500.00);
            budget1.put("spentAmount", 320.00);
            budget1.put("remaining", 180.00);
            budget1.put("percentageUsed", 64.00);

            ObjectNode budget2 = budgetsNode.addObject();
            budget2.put("category", "Entertainment");
            budget2.put("budgetAmount", 200.00);
            budget2.put("spentAmount", 150.00);
            budget2.put("remaining", 50.00);
            budget2.put("percentageUsed", 75.00);

            // Write to file
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), rootNode);

            logger.info("Budgets exported successfully to: {}", filePath);
            return new File(filePath);

        } catch (IOException e) {
            logger.error("Failed to export budgets to JSON", e);
            throw new RuntimeException("Failed to export budgets", e);
        }
    }

    @Override
    public File exportGoals(Long userId, ExportRequest request, String filePath) {
        logger.info("Exporting goals to JSON for user {}", userId);

        try {
            ObjectNode rootNode = objectMapper.createObjectNode();
            rootNode.put("exportType", "goals");
            rootNode.put("userId", userId);
            rootNode.put("generatedAt", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            rootNode.put("format", "json");

            ArrayNode goalsNode = rootNode.putArray("goals");
            
            // Add sample goal data
            ObjectNode goal1 = goalsNode.addObject();
            goal1.put("name", "Emergency Fund");
            goal1.put("targetAmount", 10000.00);
            goal1.put("currentAmount", 7500.00);
            goal1.put("progress", 75.00);
            goal1.put("targetDate", "2024-12-31");
            goal1.put("status", "In Progress");

            ObjectNode goal2 = goalsNode.addObject();
            goal2.put("name", "Vacation Fund");
            goal2.put("targetAmount", 5000.00);
            goal2.put("currentAmount", 3000.00);
            goal2.put("progress", 60.00);
            goal2.put("targetDate", "2024-06-30");
            goal2.put("status", "In Progress");

            // Write to file
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), rootNode);

            logger.info("Goals exported successfully to: {}", filePath);
            return new File(filePath);

        } catch (IOException e) {
            logger.error("Failed to export goals to JSON", e);
            throw new RuntimeException("Failed to export goals", e);
        }
    }

    @Override
    public File exportAllData(Long userId, ExportRequest request, String filePath) {
        logger.info("Exporting all financial data to JSON for user {}", userId);

        try {
            ObjectNode rootNode = objectMapper.createObjectNode();
            rootNode.put("exportType", "all");
            rootNode.put("userId", userId);
            rootNode.put("generatedAt", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            rootNode.put("format", "json");

            // Add transactions
            ArrayNode transactionsNode = rootNode.putArray("transactions");
            ObjectNode transaction = transactionsNode.addObject();
            transaction.put("date", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            transaction.put("description", "Sample Transaction");
            transaction.put("amount", 100.00);
            transaction.put("category", "Groceries");

            // Add debts
            ArrayNode debtsNode = rootNode.putArray("debts");
            ObjectNode debt = debtsNode.addObject();
            debt.put("name", "Credit Card 1");
            debt.put("currentBalance", 3200.00);
            debt.put("interestRate", 18.99);

            // Add budgets
            ArrayNode budgetsNode = rootNode.putArray("budgets");
            ObjectNode budget = budgetsNode.addObject();
            budget.put("category", "Groceries");
            budget.put("budgetAmount", 500.00);
            budget.put("spentAmount", 320.00);

            // Add goals
            ArrayNode goalsNode = rootNode.putArray("goals");
            ObjectNode goal = goalsNode.addObject();
            goal.put("name", "Emergency Fund");
            goal.put("targetAmount", 10000.00);
            goal.put("currentAmount", 7500.00);

            // Write to file
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), rootNode);

            logger.info("All financial data exported successfully to: {}", filePath);
            return new File(filePath);

        } catch (IOException e) {
            logger.error("Failed to export all data to JSON", e);
            throw new RuntimeException("Failed to export all data", e);
        }
    }
} 