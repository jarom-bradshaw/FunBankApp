package com.jarom.funbankapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    private int id;
    private int userId;
    private String accountNumber;
    private BigDecimal balance;
    private String accountType;
    private Timestamp createdAt;
}
