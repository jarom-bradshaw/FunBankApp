package com.jarom.funbankapp.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Debt payment data transfer object")
public class DebtPaymentDTO {
    
    @Schema(description = "Unique identifier for the payment")
    private Long id;
    
    @Schema(description = "ID of the debt this payment is for")
    @NotNull(message = "Debt ID is required")
    private Long debtId;
    
    @Schema(description = "Amount of the payment")
    @NotNull(message = "Payment amount is required")
    @DecimalMin(value = "0.01", message = "Payment amount must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Payment amount must have at most 10 digits and 2 decimal places")
    private BigDecimal amount;
    
    @Schema(description = "Date when the payment was made")
    private LocalDateTime paymentDate;
    
    @Schema(description = "Type of payment", example = "minimum", allowableValues = {"minimum", "extra", "full_payment", "partial"})
    @Pattern(regexp = "^(minimum|extra|full_payment|partial)$", message = "Invalid payment type")
    private String paymentType;
    
    @Schema(description = "Additional notes about the payment")
    @Size(max = 500, message = "Notes must be less than 500 characters")
    private String notes;
    
    @Schema(description = "Timestamp when the payment was recorded")
    private LocalDateTime createdAt;
    
    @Schema(description = "Timestamp when the payment was last updated")
    private LocalDateTime updatedAt;
    
    // Constructors
    public DebtPaymentDTO() {}
    
    public DebtPaymentDTO(Long debtId, BigDecimal amount, String paymentType) {
        this.debtId = debtId;
        this.amount = amount;
        this.paymentType = paymentType;
        this.paymentDate = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getDebtId() {
        return debtId;
    }
    
    public void setDebtId(Long debtId) {
        this.debtId = debtId;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }
    
    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }
    
    public String getPaymentType() {
        return paymentType;
    }
    
    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
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
        return "DebtPaymentDTO{" +
                "id=" + id +
                ", debtId=" + debtId +
                ", amount=" + amount +
                ", paymentDate=" + paymentDate +
                ", paymentType='" + paymentType + '\'' +
                '}';
    }
} 