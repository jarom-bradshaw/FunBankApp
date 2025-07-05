package com.jarom.funbankapp.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Account {
    private Long id;
    private Long userId;
    private String accountNumber;
    private BigDecimal balance;
    private String accountType;
    private String name;
    private String color;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Constructors
    public Account() {}
    
    public Account(Long id, Long userId, String accountNumber, BigDecimal balance, String accountType, String name, String color, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.userId = userId;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.accountType = accountType;
        this.name = name;
        this.color = color;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Manual getters and setters to ensure availability
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getAccountNumber() { return accountNumber; }
    public BigDecimal getBalance() { return balance; }
    public String getAccountType() { return accountType; }
    public String getName() { return name; }
    public String getColor() { return color; }
    public Timestamp getCreatedAt() { return createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    
    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
    public void setName(String name) { this.name = name; }
    public void setColor(String color) { this.color = color; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    
    // Alias method for tests that expect setType
    public void setType(String type) { this.accountType = type; }
    
    // Should add type that sets dependencies on fees, etc. according to the type set(e.g. checkings, savings, etc.)
}
