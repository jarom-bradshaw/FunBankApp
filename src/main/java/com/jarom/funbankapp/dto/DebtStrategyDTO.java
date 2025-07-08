package com.jarom.funbankapp.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Debt strategy data transfer object")
public class DebtStrategyDTO {
    
    @Schema(description = "Unique identifier for the strategy")
    private Long id;
    
    @Schema(description = "Name of the debt repayment strategy")
    @NotBlank(message = "Strategy name is required")
    @Size(min = 1, max = 100, message = "Strategy name must be between 1 and 100 characters")
    private String name;
    
    @Schema(description = "Type of strategy", example = "snowball", allowableValues = {"snowball", "avalanche", "hybrid", "custom"})
    @NotBlank(message = "Strategy type is required")
    @Pattern(regexp = "^(snowball|avalanche|hybrid|custom)$", message = "Invalid strategy type")
    private String strategyType;
    
    @Schema(description = "Description of the strategy")
    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;
    
    @Schema(description = "Total monthly amount allocated for debt repayment")
    private BigDecimal monthlyPaymentAmount;
    
    @Schema(description = "Whether this strategy is currently active")
    private Boolean isActive;
    
    @Schema(description = "List of debt IDs included in this strategy")
    private List<Long> debtIds;
    
    @Schema(description = "Timestamp when the strategy was created")
    private LocalDateTime createdAt;
    
    @Schema(description = "Timestamp when the strategy was last updated")
    private LocalDateTime updatedAt;
    
    // Constructors
    public DebtStrategyDTO() {}
    
    public DebtStrategyDTO(String name, String strategyType, String description) {
        this.name = name;
        this.strategyType = strategyType;
        this.description = description;
        this.isActive = false;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getStrategyType() {
        return strategyType;
    }
    
    public void setStrategyType(String strategyType) {
        this.strategyType = strategyType;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getMonthlyPaymentAmount() {
        return monthlyPaymentAmount;
    }
    
    public void setMonthlyPaymentAmount(BigDecimal monthlyPaymentAmount) {
        this.monthlyPaymentAmount = monthlyPaymentAmount;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public List<Long> getDebtIds() {
        return debtIds;
    }
    
    public void setDebtIds(List<Long> debtIds) {
        this.debtIds = debtIds;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "DebtStrategyDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", strategyType='" + strategyType + '\'' +
                ", isActive=" + isActive +
                '}';
    }
} 