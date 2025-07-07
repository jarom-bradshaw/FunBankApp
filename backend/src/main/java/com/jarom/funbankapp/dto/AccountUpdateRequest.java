package com.jarom.funbankapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Request for updating an existing account")
public class AccountUpdateRequest {

    @Schema(description = "Account name", example = "Updated Checking Account")
    private String name;

    @Schema(description = "Account type", example = "checking", allowableValues = {"checking", "savings", "credit", "investment"})
    @Pattern(regexp = "^(checking|savings|credit|investment)$", message = "Account type must be checking, savings, credit, or investment")
    private String accountType;

    @Schema(description = "Account color for UI", example = "#FF5733")
    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Color must be a valid hex color code")
    private String color;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
} 