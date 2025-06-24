package com.jarom.funbankapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Goal {
    private int id;
    private int userId;
    private String name;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private Timestamp deadline;
    private String type; // short-term, long-term
    private String status; // active, completed, cancelled
    private Timestamp createdAt;
    private Timestamp updatedAt;
} 