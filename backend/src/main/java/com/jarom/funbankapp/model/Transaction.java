package com.jarom.funbankapp.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Transaction {
    private Long id;
    private Long accountId;
    private String type;         // "deposit", "withdraw", "transfer"
    private BigDecimal amount;
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
