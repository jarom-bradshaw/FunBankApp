package com.jarom.funbankapp.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Debt data transfer object")
public class DebtDTO {
    
    @Schema(description = "Unique identifier for the debt")
    private Long id;
    
    @Schema(description = "Name or description of the debt")
    @NotBlank(message = "Debt name is required")
    @Size(min = 1, max = 100, message = "Debt name must be between 1 and 100 characters")
    private String name;
    
    @Schema(description = "Type of debt", example = "credit_card", allowableValues = {"credit_card", "student_loan", "mortgage", "personal_loan", "car_loan", "medical_debt", "other"})
    @NotBlank(message = "Debt type is required")
    @Pattern(regexp = "^(credit_card|student_loan|mortgage|personal_loan|car_loan|medical_debt|other)$", message = "Invalid debt type")
    private String debtType;
    
    @Schema(description = "Original amount of the debt")
    @NotNull(message = "Original amount is required")
    @DecimalMin(value = "0.01", message = "Original amount must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Original amount must have at most 10 digits and 2 decimal places")
    private BigDecimal originalAmount;
    
    @Schema(description = "Current remaining balance")
    @DecimalMin(value = "0.00", message = "Current balance cannot be negative")
    @Digits(integer = 10, fraction = 2, message = "Current balance must have at most 10 digits and 2 decimal places")
    private BigDecimal currentBalance;
    
    @Schema(description = "Annual interest rate as a percentage")
    @DecimalMin(value = "0.00", message = "Interest rate cannot be negative")
    @Digits(integer = 3, fraction = 2, message = "Interest rate must have at most 3 digits and 2 decimal places")
    private BigDecimal interestRate;
    
    @Schema(description = "Minimum monthly payment required")
    @DecimalMin(value = "0.00", message = "Minimum payment cannot be negative")
    @Digits(integer = 10, fraction = 2, message = "Minimum payment must have at most 10 digits and 2 decimal places")
    private BigDecimal minimumPayment;
    
    @Schema(description = "Day of month when payment is due")
    @Min(value = 1, message = "Due date must be between 1 and 31")
    @Max(value = 31, message = "Due date must be between 1 and 31")
    private Integer dueDate;
    
    @Schema(description = "Date when the debt was taken on")
    private LocalDateTime startDate;
    
    @Schema(description = "Expected end date of the debt")
    private LocalDateTime endDate;
    
    @Schema(description = "Status of the debt", example = "active", allowableValues = {"active", "paid_off", "defaulted", "in_collections", "settled"})
    @Pattern(regexp = "^(active|paid_off|defaulted|in_collections|settled)$", message = "Invalid debt status")
    private String status;
    
    @Schema(description = "Priority level for repayment", example = "high", allowableValues = {"high", "medium", "low"})
    @Pattern(regexp = "^(high|medium|low)$", message = "Priority must be high, medium, or low")
    private String priority;
    
    @Schema(description = "Additional notes about the debt")
    @Size(max = 500, message = "Notes must be less than 500 characters")
    private String notes;
    
    @Schema(description = "Timestamp when the debt was created")
    private LocalDateTime createdAt;
    
    @Schema(description = "Timestamp when the debt was last updated")
    private LocalDateTime updatedAt;
    
    // Constructors
    public DebtDTO() {}
    
    public DebtDTO(String name, String debtType, BigDecimal originalAmount, BigDecimal interestRate, 
                   BigDecimal minimumPayment, Integer dueDate) {
        this.name = name;
        this.debtType = debtType;
        this.originalAmount = originalAmount;
        this.currentBalance = originalAmount;
        this.interestRate = interestRate;
        this.minimumPayment = minimumPayment;
        this.dueDate = dueDate;
        this.status = "active";
        this.priority = "medium";
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    
    public LocalDateTime getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }
    
    public LocalDateTime getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDateTime endDate) {
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
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "DebtDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", debtType='" + debtType + '\'' +
                ", originalAmount=" + originalAmount +
                ", currentBalance=" + currentBalance +
                ", interestRate=" + interestRate +
                ", status='" + status + '\'' +
                '}';
    }
} 