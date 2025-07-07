package com.jarom.funbankapp.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class Transaction {
    private Long id;
    
    @NotNull(message = "Account ID is required")
    @Positive(message = "Account ID must be positive")
    private Long accountId;
    
    @NotBlank(message = "Transaction type is required")
    @Pattern(regexp = "^(deposit|withdraw|transfer)$", message = "Transaction type must be one of: deposit, withdraw, transfer")
    private String type;         // "deposit", "withdraw", "transfer"
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", inclusive = true, message = "Amount must be greater than 0")
    @Digits(integer = 15, fraction = 2, message = "Amount must have at most 15 digits and 2 decimal places")
    private BigDecimal amount;
    
    @Size(max = 50, message = "Category cannot exceed 50 characters")
    private String category;
    
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;
    
    private Timestamp transactionDate;
    private Timestamp createdAt;
    
    // Constructors
    public Transaction() {}
    
    public Transaction(Long id, Long accountId, String type, BigDecimal amount, String category, String description, Timestamp transactionDate, Timestamp createdAt) {
        this.id = id;
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.transactionDate = transactionDate;
        this.createdAt = createdAt;
    }
    
    // Manual getters and setters
    public Long getId() { return id; }
    public Long getAccountId() { return accountId; }
    public String getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public Timestamp getTransactionDate() { return transactionDate; }
    public Timestamp getCreatedAt() { return createdAt; }
    
    public void setId(Long id) { this.id = id; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }
    public void setType(String type) { this.type = type; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setCategory(String category) { this.category = category; }
    public void setDescription(String description) { this.description = description; }
    public void setTransactionDate(Timestamp transactionDate) { this.transactionDate = transactionDate; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
