package com.jarom.funbankapp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Request for updating an existing goal")
public class GoalUpdateRequest {

    @Schema(description = "Goal name", example = "Updated Vacation Fund")
    private String name;

    @Schema(description = "Goal description", example = "Updated goal for summer vacation")
    private String description;

    @Schema(description = "Target amount to save", example = "5000.00")
    @DecimalMin(value = "0.01", message = "Target amount must be greater than 0")
    private BigDecimal targetAmount;

    @Schema(description = "Current amount saved", example = "1500.00")
    @DecimalMin(value = "0.00", message = "Current amount cannot be negative")
    private BigDecimal currentAmount;

    @Schema(description = "Goal deadline", example = "2024-12-31")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deadline;

    @Schema(description = "Goal type", example = "savings", allowableValues = {"savings", "debt", "investment", "emergency"})
    @Pattern(regexp = "^(savings|debt|investment|emergency)$", message = "Type must be savings, debt, investment, or emergency")
    private String type;

    @Schema(description = "Goal status", example = "active", allowableValues = {"active", "completed", "paused", "cancelled"})
    @Pattern(regexp = "^(active|completed|paused|cancelled)$", message = "Status must be active, completed, paused, or cancelled")
    private String status;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }

    public BigDecimal getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(BigDecimal currentAmount) {
        this.currentAmount = currentAmount;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
} 