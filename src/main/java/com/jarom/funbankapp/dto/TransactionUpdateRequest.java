package com.jarom.funbankapp.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Request for updating an existing transaction")
public class TransactionUpdateRequest {

    @Schema(description = "Transaction amount", example = "150.00")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @Schema(description = "Transaction description", example = "Updated grocery shopping")
    private String description;

    @Schema(description = "Transaction category", example = "Food & Dining")
    private String category;

    @Schema(description = "Transaction type", example = "deposit", allowableValues = {"deposit", "withdraw", "transfer"})
    @Pattern(regexp = "^(deposit|withdraw|transfer)$", message = "Type must be deposit, withdraw, or transfer")
    private String type;

    @Schema(description = "Transaction date and time", example = "2024-01-15T10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime transactionDate;

    @Schema(description = "Account ID for the transaction", example = "1")
    private Long accountId;

    // Default constructor
    public TransactionUpdateRequest() {}

    // Constructor with all fields
    public TransactionUpdateRequest(BigDecimal amount, String description, String category, 
                                  String type, LocalDateTime transactionDate, Long accountId) {
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.type = type;
        this.transactionDate = transactionDate;
        this.accountId = accountId;
    }

    // Getters and Setters
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    @Override
    public String toString() {
        return "TransactionUpdateRequest{" +
                "amount=" + amount +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", type='" + type + '\'' +
                ", transactionDate=" + transactionDate +
                ", accountId=" + accountId +
                '}';
    }
} 