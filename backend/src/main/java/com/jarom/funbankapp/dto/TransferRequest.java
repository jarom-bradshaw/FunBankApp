package com.jarom.funbankapp.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request object for transferring funds between accounts")
public class TransferRequest {
    
    @Schema(description = "ID of the source account to transfer from", example = "1", required = true)
    private Long fromAccountId;
    
    @Schema(description = "ID of the destination account to transfer to", example = "2", required = true)
    private Long toAccountId;
    
    @Schema(description = "Amount to transfer", example = "75.00", required = true, minimum = "0.01")
    private BigDecimal amount;
    
    @Schema(description = "Optional description for the transfer", example = "Transfer to savings", required = false)
    private String description;

    // Getters
    public Long getFromAccountId() { return fromAccountId; }
    public Long getToAccountId() { return toAccountId; }
    public BigDecimal getAmount() { return amount; }
    public String getDescription() { return description; }

    // Setters
    public void setFromAccountId(Long fromAccountId) { this.fromAccountId = fromAccountId; }
    public void setToAccountId(Long toAccountId) { this.toAccountId = toAccountId; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setDescription(String description) { this.description = description; }
}
