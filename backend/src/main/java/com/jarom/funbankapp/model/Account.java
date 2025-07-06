package com.jarom.funbankapp.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "Bank account entity")
public class Account {
    
    @Schema(description = "Unique identifier for the account", example = "1")
    private Long id;
    
    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    @Schema(description = "ID of the user who owns this account", example = "123", required = true)
    private Long userId;
    
    @NotBlank(message = "Account number is required")
    @Size(max = 50, message = "Account number cannot exceed 50 characters")
    @Schema(description = "Unique account number", example = "1234567890", required = true, maxLength = 50)
    private String accountNumber;
    
    @NotNull(message = "Balance is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Balance cannot be negative")
    @Digits(integer = 15, fraction = 2, message = "Balance must have at most 15 digits and 2 decimal places")
    @Schema(description = "Current account balance", example = "1250.75", required = true, minimum = "0.0")
    private BigDecimal balance;
    
    @NotBlank(message = "Account type is required")
    @Pattern(regexp = "^(checking|savings|credit|investment)$", message = "Account type must be one of: checking, savings, credit, investment")
    @Schema(description = "Type of account", example = "checking", required = true, allowableValues = {"checking", "savings", "credit", "investment"})
    private String accountType;
    
    @NotBlank(message = "Account name is required")
    @Size(min = 1, max = 100, message = "Account name must be between 1 and 100 characters")
    @Schema(description = "User-friendly name for the account", example = "Main Checking Account", required = true, maxLength = 100)
    private String name;
    
    @Size(max = 7, message = "Color code cannot exceed 7 characters")
    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Color must be a valid hex color code")
    @Schema(description = "Hex color code for UI display", example = "#FF6B6B", maxLength = 7, pattern = "^#[0-9A-Fa-f]{6}$")
    private String color;
    
    @Schema(description = "Timestamp when account was created", example = "2024-01-15T10:30:00Z")
    private Timestamp createdAt;
    
    @Schema(description = "Timestamp when account was last updated", example = "2024-01-15T10:30:00Z")
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
