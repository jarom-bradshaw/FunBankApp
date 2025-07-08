package com.jarom.funbankapp.model;

import java.sql.Timestamp;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import io.swagger.v3.oas.annotations.media.Schema;

@Table("debt_strategies")
@Schema(description = "Debt strategy entity representing a debt repayment strategy")
public class DebtStrategy {
    
    @Id
    @Column("id")
    @Schema(description = "Unique identifier for the strategy")
    private Long id;
    
    @Column("user_id")
    @Schema(description = "ID of the user who owns this strategy")
    private Long userId;
    
    @Column("name")
    @Schema(description = "Name of the debt repayment strategy")
    private String name;
    
    @Column("strategy_type")
    @Schema(description = "Type of strategy (snowball, avalanche, hybrid, custom)")
    private String strategyType;
    
    @Column("description")
    @Schema(description = "Description of the strategy")
    private String description;
    
    @Column("monthly_payment_amount")
    @Schema(description = "Total monthly amount allocated for debt repayment")
    private String monthlyPaymentAmount;
    
    @Column("is_active")
    @Schema(description = "Whether this strategy is currently active")
    private Boolean isActive;
    
    @Column("created_at")
    @Schema(description = "Timestamp when the strategy was created")
    private Timestamp createdAt;
    
    @Column("updated_at")
    @Schema(description = "Timestamp when the strategy was last updated")
    private Timestamp updatedAt;
    
    // Constructors
    public DebtStrategy() {}
    
    public DebtStrategy(Long userId, String name, String strategyType, String description) {
        this.userId = userId;
        this.name = name;
        this.strategyType = strategyType;
        this.description = description;
        this.isActive = true;
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getStrategyType() {
        return strategyType;
    }
    
    public void setStrategyType(String strategyType) {
        this.strategyType = strategyType;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getMonthlyPaymentAmount() {
        return monthlyPaymentAmount;
    }
    
    public void setMonthlyPaymentAmount(String monthlyPaymentAmount) {
        this.monthlyPaymentAmount = monthlyPaymentAmount;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "DebtStrategy{" +
                "id=" + id +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", strategyType='" + strategyType + '\'' +
                ", isActive=" + isActive +
                '}';
    }
} 