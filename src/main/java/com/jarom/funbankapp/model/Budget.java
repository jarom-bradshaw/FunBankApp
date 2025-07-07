package com.jarom.funbankapp.model;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class Budget {
    private Long id;
    
    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    private Long userId;
    
    @NotBlank(message = "Budget name is required")
    @Size(min = 1, max = 100, message = "Budget name must be between 1 and 100 characters")
    private String name;
    
    @NotBlank(message = "Category is required")
    @Size(max = 50, message = "Category cannot exceed 50 characters")
    private String category;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", inclusive = true, message = "Amount must be greater than 0")
    @Digits(integer = 15, fraction = 2, message = "Amount must have at most 15 digits and 2 decimal places")
    private BigDecimal amount;
    
    @NotBlank(message = "Period is required")
    @Pattern(regexp = "^(monthly|yearly)$", message = "Period must be either 'monthly' or 'yearly'")
    private String period; // monthly, yearly
    
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;
    
    private BigDecimal spent;
    
    private Timestamp startDate;
    private Timestamp endDate;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Constructors
    public Budget() {}
    
    public Budget(Long id, Long userId, String name, String category, BigDecimal amount, String period, String description, Timestamp startDate, Timestamp endDate, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.category = category;
        this.amount = amount;
        this.period = period;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Manual getters and setters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public BigDecimal getAmount() { return amount; }
    public String getPeriod() { return period; }
    public String getDescription() { return description; }
    public BigDecimal getSpent() { return spent; }
    public void setSpent(BigDecimal spent) { this.spent = spent; }
    public Timestamp getStartDate() { return startDate; }
    public Timestamp getEndDate() { return endDate; }
    public Timestamp getCreatedAt() { return createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    
    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setPeriod(String period) { this.period = period; }
    public void setDescription(String description) { this.description = description; }
    public void setStartDate(Timestamp startDate) { this.startDate = startDate; }
    public void setEndDate(Timestamp endDate) { this.endDate = endDate; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
} 