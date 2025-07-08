package com.jarom.funbankapp.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import io.swagger.v3.oas.annotations.media.Schema;

@Table("debts")
@Schema(description = "Debt entity representing a user's debt")
public class Debt {
    
    @Id
    @Column("id")
    @Schema(description = "Unique identifier for the debt")
    private Long id;
    
    @Column("user_id")
    @Schema(description = "ID of the user who owns this debt")
    private Long userId;
    
    @Column("name")
    @Schema(description = "Name or description of the debt")
    private String name;
    
    @Column("debt_type")
    @Schema(description = "Type of debt (credit_card, student_loan, mortgage, personal_loan, etc.)")
    private String debtType;
    
    @Column("original_amount")
    @Schema(description = "Original amount of the debt")
    private BigDecimal originalAmount;
    
    @Column("current_balance")
    @Schema(description = "Current remaining balance")
    private BigDecimal currentBalance;
    
    @Column("interest_rate")
    @Schema(description = "Annual interest rate as a percentage")
    private BigDecimal interestRate;
    
    @Column("minimum_payment")
    @Schema(description = "Minimum monthly payment required")
    private BigDecimal minimumPayment;
    
    @Column("due_date")
    @Schema(description = "Day of month when payment is due")
    private Integer dueDate;
    
    @Column("start_date")
    @Schema(description = "Date when the debt was taken on")
    private Timestamp startDate;
    
    @Column("end_date")
    @Schema(description = "Expected end date of the debt")
    private Timestamp endDate;
    
    @Column("status")
    @Schema(description = "Status of the debt (active, paid_off, defaulted, etc.)")
    private String status;
    
    @Column("priority")
    @Schema(description = "Priority level for repayment (high, medium, low)")
    private String priority;
    
    @Column("notes")
    @Schema(description = "Additional notes about the debt")
    private String notes;
    
    @Column("created_at")
    @Schema(description = "Timestamp when the debt was created")
    private Timestamp createdAt;
    
    @Column("updated_at")
    @Schema(description = "Timestamp when the debt was last updated")
    private Timestamp updatedAt;
    
    // Constructors
    public Debt() {}
    
    public Debt(Long userId, String name, String debtType, BigDecimal originalAmount, 
                BigDecimal interestRate, BigDecimal minimumPayment, Integer dueDate) {
        this.userId = userId;
        this.name = name;
        this.debtType = debtType;
        this.originalAmount = originalAmount;
        this.currentBalance = originalAmount;
        this.interestRate = interestRate;
        this.minimumPayment = minimumPayment;
        this.dueDate = dueDate;
        this.status = "active";
        this.priority = "medium";
        this.startDate = new Timestamp(System.currentTimeMillis());
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
    
    public String getDebtType() {
        return debtType;
    }
    
    public void setDebtType(String debtType) {
        this.debtType = debtType;
    }
    
    public BigDecimal getOriginalAmount() {
        return originalAmount;
    }
    
    public void setOriginalAmount(BigDecimal originalAmount) {
        this.originalAmount = originalAmount;
    }
    
    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }
    
    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }
    
    public BigDecimal getInterestRate() {
        return interestRate;
    }
    
    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }
    
    public BigDecimal getMinimumPayment() {
        return minimumPayment;
    }
    
    public void setMinimumPayment(BigDecimal minimumPayment) {
        this.minimumPayment = minimumPayment;
    }
    
    public Integer getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(Integer dueDate) {
        this.dueDate = dueDate;
    }
    
    public Timestamp getStartDate() {
        return startDate;
    }
    
    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }
    
    public Timestamp getEndDate() {
        return endDate;
    }
    
    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getPriority() {
        return priority;
    }
    
    public void setPriority(String priority) {
        this.priority = priority;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
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
    
    // Business logic methods
    public boolean isPaidOff() {
        return "paid_off".equals(status) || currentBalance.compareTo(BigDecimal.ZERO) <= 0;
    }
    
    public BigDecimal getRemainingBalance() {
        return currentBalance.max(BigDecimal.ZERO);
    }
    
    public BigDecimal getTotalPaid() {
        return originalAmount.subtract(currentBalance);
    }
    
    @Override
    public String toString() {
        return "Debt{" +
                "id=" + id +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", debtType='" + debtType + '\'' +
                ", originalAmount=" + originalAmount +
                ", currentBalance=" + currentBalance +
                ", interestRate=" + interestRate +
                ", status='" + status + '\'' +
                '}';
    }
} 