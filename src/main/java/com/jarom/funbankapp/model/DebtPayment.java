package com.jarom.funbankapp.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import io.swagger.v3.oas.annotations.media.Schema;

@Table("debt_payments")
@Schema(description = "Debt payment entity representing a payment made towards a debt")
public class DebtPayment {
    
    @Id
    @Column("id")
    @Schema(description = "Unique identifier for the payment")
    private Long id;
    
    @Column("debt_id")
    @Schema(description = "ID of the debt this payment is for")
    private Long debtId;
    
    @Column("amount")
    @Schema(description = "Amount of the payment")
    private BigDecimal amount;
    
    @Column("payment_date")
    @Schema(description = "Date when the payment was made")
    private Timestamp paymentDate;
    
    @Column("payment_type")
    @Schema(description = "Type of payment (minimum, extra, full_payment, etc.)")
    private String paymentType;
    
    @Column("notes")
    @Schema(description = "Additional notes about the payment")
    private String notes;
    
    @Column("created_at")
    @Schema(description = "Timestamp when the payment was recorded")
    private Timestamp createdAt;
    
    @Column("updated_at")
    @Schema(description = "Timestamp when the payment was last updated")
    private Timestamp updatedAt;
    
    // Constructors
    public DebtPayment() {}
    
    public DebtPayment(Long debtId, BigDecimal amount, String paymentType) {
        this.debtId = debtId;
        this.amount = amount;
        this.paymentType = paymentType;
        this.paymentDate = new Timestamp(System.currentTimeMillis());
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
    
    public Timestamp getPaymentDate() {
        return paymentDate;
    }
    
    public void setPaymentDate(Timestamp paymentDate) {
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
        return "DebtPayment{" +
                "id=" + id +
                ", debtId=" + debtId +
                ", amount=" + amount +
                ", paymentDate=" + paymentDate +
                ", paymentType='" + paymentType + '\'' +
                '}';
    }
} 