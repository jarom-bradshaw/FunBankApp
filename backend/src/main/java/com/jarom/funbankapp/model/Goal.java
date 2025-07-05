package com.jarom.funbankapp.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Goal {
    private Long id;
    private Long userId;
    private String name;
    private String description;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private Timestamp deadline;
    private String type; // short-term, long-term
    private String status; // active, completed, cancelled
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Constructors
    public Goal() {}
    
    public Goal(Long id, Long userId, String name, String description, BigDecimal targetAmount, BigDecimal currentAmount, Timestamp deadline, String type, String status, Timestamp createdAt, Timestamp updatedAt) {
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
    
    // Manual getters and setters to ensure availability
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getTargetAmount() { return targetAmount; }
    public BigDecimal getCurrentAmount() { return currentAmount; }
    public Timestamp getDeadline() { return deadline; }
    public String getType() { return type; }
    public String getStatus() { return status; }
    public Timestamp getCreatedAt() { return createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    
    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setTargetAmount(BigDecimal targetAmount) { this.targetAmount = targetAmount; }
    public void setCurrentAmount(BigDecimal currentAmount) { this.currentAmount = currentAmount; }
    public void setDeadline(Timestamp deadline) { this.deadline = deadline; }
    public void setType(String type) { this.type = type; }
    public void setStatus(String status) { this.status = status; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
} 