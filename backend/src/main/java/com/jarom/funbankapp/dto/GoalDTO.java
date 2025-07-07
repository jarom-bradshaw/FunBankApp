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

public class GoalDTO {
    private Long id;
    
    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    private Long userId;
    
    @NotBlank(message = "Goal name is required")
    @Size(min = 1, max = 100, message = "Goal name must be between 1 and 100 characters")
    private String name;
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    
    @NotNull(message = "Target amount is required")
    @DecimalMin(value = "0.01", inclusive = true, message = "Target amount must be greater than 0")
    @Digits(integer = 15, fraction = 2, message = "Target amount must have at most 15 digits and 2 decimal places")
    private BigDecimal targetAmount;
    
    @NotNull(message = "Current amount is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Current amount cannot be negative")
    @Digits(integer = 15, fraction = 2, message = "Current amount must have at most 15 digits and 2 decimal places")
    private BigDecimal currentAmount;
    
    private LocalDate deadline;
    
    @NotBlank(message = "Goal type is required")
    @Pattern(regexp = "^(short-term|long-term)$", message = "Goal type must be either 'short-term' or 'long-term'")
    private String type; // short-term, long-term
    
    @NotBlank(message = "Goal status is required")
    @Pattern(regexp = "^(active|completed|cancelled)$", message = "Goal status must be one of: active, completed, cancelled")
    private String status; // active, completed, cancelled
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public GoalDTO() {}
    
    public GoalDTO(Long id, Long userId, String name, String description, BigDecimal targetAmount, 
                   BigDecimal currentAmount, LocalDate deadline, String type, String status, 
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.deadline = deadline;
        this.type = type;
        this.status = status;
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
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getTargetAmount() { return targetAmount; }
    public void setTargetAmount(BigDecimal targetAmount) { this.targetAmount = targetAmount; }
    
    public BigDecimal getCurrentAmount() { return currentAmount; }
    public void setCurrentAmount(BigDecimal currentAmount) { this.currentAmount = currentAmount; }
    
    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
} 