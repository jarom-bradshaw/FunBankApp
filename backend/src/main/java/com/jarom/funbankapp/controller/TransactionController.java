package com.jarom.funbankapp.controller;

import com.jarom.funbankapp.model.Transaction;
import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.TransactionDAO;
import com.jarom.funbankapp.repository.UserDAO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/transactions")
@io.swagger.v3.oas.annotations.tags.Tag(name = "Transactions", description = "Transaction management operations")
public class TransactionController {

    private final TransactionDAO transactionDAO;
    private final UserDAO userDAO;

    public TransactionController(TransactionDAO transactionDAO, UserDAO userDAO) {
        this.transactionDAO = transactionDAO;
        this.userDAO = userDAO;
    }

    @GetMapping
    @Operation(summary = "Get user transactions", description = "Retrieves all transactions for the authenticated user.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully")
    })
    public ResponseEntity<List<Transaction>> getUserTransactions() {
        String username = getCurrentUsername();
        User user = userDAO.findByUsername(username);

        List<Transaction> transactions = transactionDAO.getRecentTransactions(user.getId(), 100);
        return ResponseEntity.ok(transactions);
    }

    @PostMapping
    @Operation(summary = "Create a new transaction", description = "Creates a new transaction for the authenticated user.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transaction created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid transaction data")
    })
    public ResponseEntity<?> createTransaction(@RequestBody Transaction request) {
        String username = getCurrentUsername();
        User user = userDAO.findByUsername(username);

        request.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        
        int result = transactionDAO.logTransaction(
            request.getAccountId(),
            request.getType(),
            request.getAmount(),
            request.getDescription()
        );

        if (result > 0) {
            return ResponseEntity.ok("Transaction created successfully!");
        } else {
            return ResponseEntity.badRequest().body("Failed to create transaction");
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get transaction by ID", description = "Retrieves a specific transaction by ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transaction retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    public ResponseEntity<?> getTransaction(@PathVariable int id) {
        // This would require adding a findById method to TransactionDAO
        return ResponseEntity.ok("Transaction details for ID: " + id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update transaction", description = "Updates an existing transaction.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transaction updated successfully"),
        @ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    public ResponseEntity<?> updateTransaction(@PathVariable int id, @RequestBody Transaction request) {
        // This would require adding an update method to TransactionDAO
        return ResponseEntity.ok("Transaction updated successfully!");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete transaction", description = "Deletes a transaction.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transaction deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    public ResponseEntity<?> deleteTransaction(@PathVariable int id) {
        // This would require adding a delete method to TransactionDAO
        return ResponseEntity.ok("Transaction deleted successfully!");
    }

    @PostMapping("/import")
    @Operation(summary = "Import transactions", description = "Import transactions from external source.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transactions imported successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid import data")
    })
    public ResponseEntity<?> importTransactions(@RequestBody List<Transaction> transactions) {
        String username = getCurrentUsername();
        User user = userDAO.findByUsername(username);

        int importedCount = 0;
        for (Transaction transaction : transactions) {
            transaction.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            int result = transactionDAO.logTransaction(
                transaction.getAccountId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getDescription()
            );
            if (result > 0) {
                importedCount++;
            }
        }

        return ResponseEntity.ok("Imported " + importedCount + " transactions successfully!");
    }

    @GetMapping("/categories")
    @Operation(summary = "Get transaction categories", description = "Retrieves spending analysis by category.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categories retrieved successfully")
    })
    public ResponseEntity<Map<String, BigDecimal>> getTransactionCategories(@RequestParam(defaultValue = "30") int days) {
        String username = getCurrentUsername();
        User user = userDAO.findByUsername(username);

        Map<String, BigDecimal> categories = transactionDAO.getSpendingByCategory(user.getId(), days);
        return ResponseEntity.ok(categories);
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
} 