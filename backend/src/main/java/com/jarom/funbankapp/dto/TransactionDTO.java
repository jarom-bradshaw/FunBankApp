package com.jarom.funbankapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TransactionDTO {
    private Long id;
    
    @NotBlank(message = "Description is required")
    @Size(min = 1, max = 255, message = "Description must be between 1 and 255 characters")
    private String description;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    @NotBlank(message = "Category is required")
    @Size(min = 1, max = 100, message = "Category must be between 1 and 100 characters")
    private String category;
    
    @NotNull(message = "Account ID is required")
    private Long accountId;
    
    private String accountName;
    
    @NotNull(message = "Date is required")
    private LocalDate date;
    
    @NotNull(message = "Transaction type is required")
    private TransactionType type;
    
    @Size(max = 1000, message = "Notes must be less than 1000 characters")
    private String notes;
    
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Transaction types enum
    public enum TransactionType {
        INCOME, EXPENSE
    }

    // Constructors
    public TransactionDTO() {}

    public TransactionDTO(Long id, String description, BigDecimal amount, String category,
                         Long accountId, String accountName, LocalDate date, TransactionType type,
                         String notes, Long userId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.accountId = accountId;
        this.accountName = accountName;
        this.date = date;
        this.type = type;
        this.notes = notes;
        this.userId = userId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }

    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
} 