package com.jarom.funbankapp.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AccountDTO {
    private Long id;
    
    @NotBlank(message = "Account name is required")
    @Size(min = 1, max = 255, message = "Account name must be between 1 and 255 characters")
    private String name;
    
    @Size(max = 100, message = "Account number must be less than 100 characters")
    private String accountNumber;
    
    @NotNull(message = "Account type is required")
    private AccountType accountType;
    
    @NotNull(message = "Balance is required")
    @DecimalMin(value = "0.0", message = "Balance must be greater than or equal to 0")
    private BigDecimal balance;
    
    @Size(max = 7, message = "Color must be a valid hex color (7 characters)")
    private String color;
    
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Account types enum
    public enum AccountType {
        CHECKING, SAVINGS, CREDIT, INVESTMENT, LOAN, CASH
    }
} 