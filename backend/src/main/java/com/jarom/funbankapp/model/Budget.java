package com.jarom.funbankapp.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Budget {
    private Long id;
    private Long userId;
    private String name;
    private String category;
    private BigDecimal amount;
    private String period; // monthly, yearly
    private String description;
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