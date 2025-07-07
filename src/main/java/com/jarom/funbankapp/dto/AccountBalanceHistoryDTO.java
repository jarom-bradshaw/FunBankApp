package com.jarom.funbankapp.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AccountBalanceHistoryDTO {
    private Long id;
    private Long accountId;
    private BigDecimal balance;
    private BigDecimal changeAmount;
    private String changeType; // "deposit", "withdraw", "transfer", "adjustment"
    private String description;
    private LocalDateTime recordedAt;

    // Getters
    public Long getId() { return id; }
    public Long getAccountId() { return accountId; }
    public BigDecimal getBalance() { return balance; }
    public BigDecimal getChangeAmount() { return changeAmount; }
    public String getChangeType() { return changeType; }
    public String getDescription() { return description; }
    public LocalDateTime getRecordedAt() { return recordedAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public void setChangeAmount(BigDecimal changeAmount) { this.changeAmount = changeAmount; }
    public void setChangeType(String changeType) { this.changeType = changeType; }
    public void setDescription(String description) { this.description = description; }
    public void setRecordedAt(LocalDateTime recordedAt) { this.recordedAt = recordedAt; }
} 