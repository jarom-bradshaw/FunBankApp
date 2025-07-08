package com.jarom.funbankapp.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.jarom.funbankapp.dto.DebtDTO;
import com.jarom.funbankapp.dto.DebtPaymentDTO;

/**
 * Service layer for debt management operations
 * Handles business logic and data transformation for debts
 */
public interface DebtService {
    
    /**
     * Create a new debt for the authenticated user
     * @param debtDTO debt data transfer object
     * @return created debt DTO
     */
    DebtDTO createDebt(DebtDTO debtDTO);
    
    /**
     * Get all debts for the authenticated user
     * @return list of debt DTOs
     */
    List<DebtDTO> getUserDebts();
    
    /**
     * Get a specific debt by ID (if user owns it)
     * @param debtId the debt ID
     * @return debt DTO
     */
    DebtDTO getDebtById(Long debtId);
    
    /**
     * Update an existing debt (if user owns it)
     * @param debtId the debt ID
     * @param debtDTO updated debt data
     * @return updated debt DTO
     */
    DebtDTO updateDebt(Long debtId, DebtDTO debtDTO);
    
    /**
     * Delete a debt (if user owns it)
     * @param debtId the debt ID
     */
    void deleteDebt(Long debtId);
    
    /**
     * Make a payment towards a debt
     * @param debtId the debt ID
     * @param paymentDTO payment data
     * @return updated debt DTO
     */
    DebtDTO makePayment(Long debtId, DebtPaymentDTO paymentDTO);
    
    /**
     * Get payment history for a debt
     * @param debtId the debt ID
     * @return list of payment DTOs
     */
    List<DebtPaymentDTO> getPaymentHistory(Long debtId);
    
    /**
     * Get debt summary for the authenticated user
     * @return debt summary data
     */
    Map<String, Object> getDebtSummary();
    
    /**
     * Get debt analysis and insights
     * @return debt analysis data
     */
    Map<String, Object> getDebtAnalysis();
    
    /**
     * Calculate debt payoff timeline
     * @param monthlyPaymentAmount amount available for debt repayment
     * @param strategy repayment strategy (snowball, avalanche)
     * @return payoff timeline data
     */
    Map<String, Object> calculatePayoffTimeline(BigDecimal monthlyPaymentAmount, String strategy);
    
    /**
     * Get debts by type
     * @param debtType type of debt to filter by
     * @return list of debt DTOs
     */
    List<DebtDTO> getDebtsByType(String debtType);
    
    /**
     * Get debts by priority
     * @param priority priority level to filter by
     * @return list of debt DTOs
     */
    List<DebtDTO> getDebtsByPriority(String priority);
    
    /**
     * Get total debt amount for the user
     * @return total debt amount
     */
    BigDecimal getTotalDebtAmount();
    
    /**
     * Get total monthly minimum payments
     * @return total minimum payments
     */
    BigDecimal getTotalMinimumPayments();
    
    /**
     * Check if user owns the debt
     * @param debtId the debt ID
     * @return true if user owns the debt
     */
    boolean userOwnsDebt(Long debtId);
} 