package com.jarom.funbankapp.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TransferRequest {
    @NotNull(message = "Source account ID is required")
    private Long sourceAccountId;
    
    @NotNull(message = "Destination account ID is required")
    private Long destinationAccountId;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    @Size(max = 255, message = "Description must be less than 255 characters")
    private String description;
    
    @Size(max = 50, message = "Category must be less than 50 characters")
    private String category;

    // Default constructor
    public TransferRequest() {}

    // Constructor with required fields
    public TransferRequest(Long sourceAccountId, Long destinationAccountId, BigDecimal amount) {
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.amount = amount;
    }

    // Getters and Setters
    public Long getSourceAccountId() {
        return sourceAccountId;
    }

    public void setSourceAccountId(Long sourceAccountId) {
        this.sourceAccountId = sourceAccountId;
    }

    public Long getDestinationAccountId() {
        return destinationAccountId;
    }

    public void setDestinationAccountId(Long destinationAccountId) {
        this.destinationAccountId = destinationAccountId;
    }

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

    // Alias methods for compatibility with AccountService
    public Long getFromAccountId() {
        return sourceAccountId;
    }

    public Long getToAccountId() {
        return destinationAccountId;
    }
}
