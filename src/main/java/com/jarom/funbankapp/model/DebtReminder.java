package com.jarom.funbankapp.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import io.swagger.v3.oas.annotations.media.Schema;

@Table("debt_reminders")
@Schema(description = "Debt reminder entity representing payment reminders")
public class DebtReminder {
    
    @Id
    @Column("id")
    @Schema(description = "Unique identifier for the reminder")
    private Long id;
    
    @Column("debt_id")
    @Schema(description = "ID of the debt this reminder is for")
    private Long debtId;
    
    @Column("reminder_date")
    @Schema(description = "Date when the reminder should be sent")
    private Timestamp reminderDate;
    
    @Column("due_date")
    @Schema(description = "Date when the payment is due")
    private Timestamp dueDate;
    
    @Column("amount")
    @Schema(description = "Amount due for this payment")
    private BigDecimal amount;
    
    @Column("reminder_type")
    @Schema(description = "Type of reminder (email, sms, push, etc.)")
    private String reminderType;
    
    @Column("is_sent")
    @Schema(description = "Whether the reminder has been sent")
    private Boolean isSent;
    
    @Column("is_active")
    @Schema(description = "Whether the reminder is active")
    private Boolean isActive;
    
    @Column("sent_date")
    @Schema(description = "Date when the reminder was sent")
    private Timestamp sentDate;
    
    @Column("notes")
    @Schema(description = "Additional notes about the reminder")
    private String notes;
    
    @Column("created_at")
    @Schema(description = "Timestamp when the reminder was created")
    private Timestamp createdAt;
    
    @Column("updated_at")
    @Schema(description = "Timestamp when the reminder was last updated")
    private Timestamp updatedAt;
    
    // Constructors
    public DebtReminder() {}
    
    public DebtReminder(Long debtId, Timestamp dueDate, BigDecimal amount, String reminderType) {
        this.debtId = debtId;
        this.dueDate = dueDate;
        this.amount = amount;
        this.reminderType = reminderType;
        this.isSent = false;
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
    
    public Long getDebtId() {
        return debtId;
    }
    
    public void setDebtId(Long debtId) {
        this.debtId = debtId;
    }
    
    public Timestamp getReminderDate() {
        return reminderDate;
    }
    
    public void setReminderDate(Timestamp reminderDate) {
        this.reminderDate = reminderDate;
    }
    
    public Timestamp getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(Timestamp dueDate) {
        this.dueDate = dueDate;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getReminderType() {
        return reminderType;
    }
    
    public void setReminderType(String reminderType) {
        this.reminderType = reminderType;
    }
    
    public Boolean getIsSent() {
        return isSent;
    }
    
    public void setIsSent(Boolean isSent) {
        this.isSent = isSent;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public Timestamp getSentDate() {
        return sentDate;
    }
    
    public void setSentDate(Timestamp sentDate) {
        this.sentDate = sentDate;
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
        return "DebtReminder{" +
                "id=" + id +
                ", debtId=" + debtId +
                ", dueDate=" + dueDate +
                ", amount=" + amount +
                ", reminderType='" + reminderType + '\'' +
                ", isSent=" + isSent +
                '}';
    }
} 