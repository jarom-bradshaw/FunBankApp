package com.jarom.funbankapp.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DepositRequest {
    private int accountId;
    private BigDecimal amount;
    private String description;
}
