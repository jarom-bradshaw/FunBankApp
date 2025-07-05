package com.jarom.funbankapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Budget {
    private Long id;
    private Long userId;
    private String name;
    private String category;
    private BigDecimal amount;
    private String period; // monthly, yearly
    private Timestamp startDate;
    private Timestamp endDate;
    private Timestamp createdAt;
    private Timestamp updatedAt;
} 