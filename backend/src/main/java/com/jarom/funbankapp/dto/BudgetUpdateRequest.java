package com.jarom.funbankapp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Request for updating an existing budget")
public class BudgetUpdateRequest {

    @Schema(description = "Budget name", example = "Updated Grocery Budget")
    private String name;

    @Schema(description = "Budget category", example = "groceries")
    private String category;

    @Schema(description = "Budget amount", example = "500.00")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @Schema(description = "Budget period", example = "monthly", allowableValues = {"weekly", "monthly", "yearly"})
    @Pattern(regexp = "^(weekly|monthly|yearly)$", message = "Period must be weekly, monthly, or yearly")
    private String period;

    @Schema(description = "Budget description", example = "Updated budget for groceries and household items")
    private String description;

    @Schema(description = "Amount spent so far", example = "150.00")
    @DecimalMin(value = "0.00", message = "Spent amount cannot be negative")
    private BigDecimal spent;

    @Schema(description = "Budget start date", example = "2024-01-01")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Schema(description = "Budget end date", example = "2024-12-31")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getSpent() {
        return spent;
    }

    public void setSpent(BigDecimal spent) {
        this.spent = spent;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
} 