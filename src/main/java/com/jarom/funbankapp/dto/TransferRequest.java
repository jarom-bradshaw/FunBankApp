package com.jarom.funbankapp.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {
    private int fromAccountId;
    private int toAccountId;
    private BigDecimal amount;
    private String description;
}
