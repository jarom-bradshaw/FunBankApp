package com.jarom.funbankapp.controller;

import com.jarom.funbankapp.dto.ApiResponse;
import com.jarom.funbankapp.model.Transaction;
import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.TransactionRepository;
import com.jarom.funbankapp.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transactions", description = "Transaction management and history")
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionController(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Transaction>>> getTransactions(Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Transaction> transactions = transactionRepository.getRecentTransactions(user.getId(), 100);
            return ResponseEntity.ok(ApiResponse.success("Transactions retrieved successfully", transactions));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve transactions: " + e.getMessage()));
        }
    }

    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<Transaction>> deposit(@RequestBody Map<String, Object> request, Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            String description = (String) request.get("description");

            int result = transactionRepository.logTransaction(user.getId(), "deposit", amount, description);
            
            if (result > 0) {
                Transaction transaction = new Transaction();
                transaction.setAccountId(user.getId());
                transaction.setType("deposit");
                transaction.setAmount(amount);
                transaction.setDescription(description);
                
                return ResponseEntity.ok(ApiResponse.success("Deposit successful", transaction));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("Deposit failed"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to process deposit: " + e.getMessage()));
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<Transaction>> withdraw(@RequestBody Map<String, Object> request, Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            String description = (String) request.get("description");

            int result = transactionRepository.logTransaction(user.getId(), "withdrawal", amount, description);
            
            if (result > 0) {
                Transaction transaction = new Transaction();
                transaction.setAccountId(user.getId());
                transaction.setType("withdrawal");
                transaction.setAmount(amount);
                transaction.setDescription(description);
                
                return ResponseEntity.ok(ApiResponse.success("Withdrawal successful", transaction));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("Withdrawal failed"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to process withdrawal: " + e.getMessage()));
        }
    }

    @GetMapping("/spending")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getSpendingByCategory(Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Map<String, BigDecimal> categories = transactionRepository.getSpendingByCategory(user.getId(), 30);
            return ResponseEntity.ok(ApiResponse.success("Spending by category retrieved successfully", categories));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve spending data: " + e.getMessage()));
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTransactionSummary(Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Map<String, BigDecimal> categories = transactionRepository.getSpendingByCategory(user.getId(), 30);
            
            Map<String, Object> summary = new HashMap<>();
            summary.put("spendingByCategory", categories);
            summary.put("totalTransactions", categories.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add));
            
            return ResponseEntity.ok(ApiResponse.success("Transaction summary retrieved successfully", summary));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve transaction summary: " + e.getMessage()));
        }
    }
} 