package com.jarom.funbankapp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class BudgetDTO {
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
    
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public BudgetDTO() {}
    
    public BudgetDTO(Long id, Long userId, String name, String category, BigDecimal amount, 
                     String period, String description, BigDecimal spent, LocalDate startDate, 
                     LocalDate endDate, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.category = category;
        this.amount = amount;
        this.period = period;
        this.description = description;
        this.spent = spent;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getSpent() { return spent; }
    public void setSpent(BigDecimal spent) { this.spent = spent; }
    
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
} 