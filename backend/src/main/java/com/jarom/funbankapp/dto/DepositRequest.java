package com.jarom.funbankapp.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request object for depositing funds into an account")
public class DepositRequest {
    
    @Schema(description = "ID of the account to deposit into", example = "1", required = true)
    private Long accountId;
    
    @Schema(description = "Amount to deposit", example = "100.50", required = true, minimum = "0.01")
    private BigDecimal amount;
    
    @Schema(description = "Optional description for the deposit", example = "Salary deposit", required = false)
    private String description;

    // Getters
    public Long getAccountId() { return accountId; }
    public BigDecimal getAmount() { return amount; }
    public String getDescription() { return description; }

    // Setters
    public void setAccountId(Long accountId) { this.accountId = accountId; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setDescription(String description) { this.description = description; }
}
