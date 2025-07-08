package com.jarom.funbankapp.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Debt reminder data transfer object")
public class DebtReminderDTO {
    
    @Schema(description = "Unique identifier for the reminder")
    private Long id;
    
    @Schema(description = "ID of the debt this reminder is for")
    @NotNull(message = "Debt ID is required")
    private Long debtId;
    
    @Schema(description = "Date when the reminder should be sent")
    private LocalDateTime reminderDate;
    
    @Schema(description = "Date when the payment is due")
    @NotNull(message = "Due date is required")
    private LocalDateTime dueDate;
    
    @Schema(description = "Amount due for this payment")
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Amount must have at most 10 digits and 2 decimal places")
    private BigDecimal amount;
    
    @Schema(description = "Type of reminder", example = "email", allowableValues = {"email", "sms", "push", "in_app"})
    @NotNull(message = "Reminder type is required")
    @Pattern(regexp = "^(email|sms|push|in_app)$", message = "Invalid reminder type")
    private String reminderType;
    
    @Schema(description = "Whether the reminder has been sent")
    private Boolean isSent;
    
    @Schema(description = "Whether the reminder is active")
    private Boolean isActive;
    
    @Schema(description = "Date when the reminder was sent")
    private LocalDateTime sentDate;
    
    @Schema(description = "Additional notes about the reminder")
    @Size(max = 500, message = "Notes must be less than 500 characters")
    private String notes;
    
    @Schema(description = "Timestamp when the reminder was created")
    private LocalDateTime createdAt;
    
    @Schema(description = "Timestamp when the reminder was last updated")
    private LocalDateTime updatedAt;
    
    // Constructors
    public DebtReminderDTO() {}
    
    public DebtReminderDTO(Long debtId, LocalDateTime dueDate, BigDecimal amount, String reminderType) {
        this.debtId = debtId;
        this.dueDate = dueDate;
        this.amount = amount;
        this.reminderType = reminderType;
        this.isSent = false;
        this.isActive = true;
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
    
    public LocalDateTime getReminderDate() {
        return reminderDate;
    }
    
    public void setReminderDate(LocalDateTime reminderDate) {
        this.reminderDate = reminderDate;
    }
    
    public LocalDateTime getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(LocalDateTime dueDate) {
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
    
    public LocalDateTime getSentDate() {
        return sentDate;
    }
    
    public void setSentDate(LocalDateTime sentDate) {
        this.sentDate = sentDate;
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
        return "DebtReminderDTO{" +
                "id=" + id +
                ", debtId=" + debtId +
                ", dueDate=" + dueDate +
                ", amount=" + amount +
                ", reminderType='" + reminderType + '\'' +
                ", isSent=" + isSent +
                ", isActive=" + isActive +
                '}';
    }
} 