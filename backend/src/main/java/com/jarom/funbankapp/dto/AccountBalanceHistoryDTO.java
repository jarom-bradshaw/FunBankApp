package com.jarom.funbankapp.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AccountBalanceHistoryDTO {
    private Long id;
    private Long accountId;
    private BigDecimal balance;
    private BigDecimal changeAmount;
    private String changeType; // "deposit", "withdraw", "transfer", "adjustment"
    private String description;
    private LocalDateTime recordedAt;
} 