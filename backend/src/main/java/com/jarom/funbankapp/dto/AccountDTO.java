package com.jarom.funbankapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AccountDTO {
    private Long id;
    
    @NotBlank(message = "Account name is required")
    @Size(min = 1, max = 255, message = "Account name must be between 1 and 255 characters")
    private String name;
    
    @Size(max = 100, message = "Account number must be less than 100 characters")
    private String accountNumber;
    
    @NotNull(message = "Account type is required")
    private AccountType accountType;
    
    @NotNull(message = "Balance is required")
    @DecimalMin(value = "0.0", message = "Balance must be greater than or equal to 0")
    private BigDecimal balance;
    
    @Size(max = 7, message = "Color must be a valid hex color (7 characters)")
    private String color;
    
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getAccountNumber() { return accountNumber; }
    public AccountType getAccountType() { return accountType; }
    public BigDecimal getBalance() { return balance; }
    public String getColor() { return color; }
    public Long getUserId() { return userId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public void setAccountType(AccountType accountType) { this.accountType = accountType; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public void setColor(String color) { this.color = color; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Account types enum
    public enum AccountType {
        CHECKING, SAVINGS, CREDIT, INVESTMENT, LOAN, CASH
    }
} 