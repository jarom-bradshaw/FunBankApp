package com.jarom.funbankapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    private int id;
    private int accountId;
    private String type;         // "deposit", "withdraw", "transfer"
    private BigDecimal amount;
    private String description;
    private Timestamp createdAt;
}
