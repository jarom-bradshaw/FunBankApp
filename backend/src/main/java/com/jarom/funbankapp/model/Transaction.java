package com.jarom.funbankapp.model;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

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
    
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;
    
    private Timestamp createdAt;
    
    // Constructors
    public Transaction() {}
    
    public Transaction(Long id, Long accountId, String type, BigDecimal amount, String description, Timestamp createdAt) {
        this.id = id;
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.createdAt = createdAt;
    }
    
    // Manual getters and setters
    public Long getId() { return id; }
    public Long getAccountId() { return accountId; }
    public String getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public String getDescription() { return description; }
    public Timestamp getCreatedAt() { return createdAt; }
    
    public void setId(Long id) { this.id = id; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }
    public void setType(String type) { this.type = type; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setDescription(String description) { this.description = description; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
