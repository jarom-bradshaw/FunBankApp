package com.jarom.funbankapp.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.jarom.funbankapp.dto.DebtStrategyDTO;

/**
 * Service layer for debt strategy operations
 * Handles business logic for debt repayment strategies
 */
public interface DebtStrategyService {
    
    /**
     * Create a new debt strategy for the authenticated user
     * @param strategyDTO strategy data transfer object
     * @return created strategy DTO
     */
    DebtStrategyDTO createStrategy(DebtStrategyDTO strategyDTO);
    
    /**
     * Get all strategies for the authenticated user
     * @return list of strategy DTOs
     */
    List<DebtStrategyDTO> getUserStrategies();
    
    /**
     * Get a specific strategy by ID (if user owns it)
     * @param strategyId the strategy ID
     * @return strategy DTO
     */
    DebtStrategyDTO getStrategyById(Long strategyId);
    
    /**
     * Update an existing strategy (if user owns it)
     * @param strategyId the strategy ID
     * @param strategyDTO updated strategy data
     * @return updated strategy DTO
     */
    DebtStrategyDTO updateStrategy(Long strategyId, DebtStrategyDTO strategyDTO);
    
    /**
     * Delete a strategy (if user owns it)
     * @param strategyId the strategy ID
     */
    void deleteStrategy(Long strategyId);
    
    /**
     * Activate a strategy (deactivates others)
     * @param strategyId the strategy ID
     * @return activated strategy DTO
     */
    DebtStrategyDTO activateStrategy(Long strategyId);
    
    /**
     * Get the currently active strategy for the user
     * @return active strategy DTO or null
     */
    DebtStrategyDTO getActiveStrategy();
    
    /**
     * Generate a snowball strategy recommendation
     * @return strategy recommendation data
     */
    Map<String, Object> generateSnowballStrategy();
    
    /**
     * Generate an avalanche strategy recommendation
     * @return strategy recommendation data
     */
    Map<String, Object> generateAvalancheStrategy();
    
    /**
     * Calculate strategy effectiveness
     * @param strategyId the strategy ID
     * @return effectiveness analysis data
     */
    Map<String, Object> calculateStrategyEffectiveness(Long strategyId);
    
    /**
     * Get strategy comparison
     * @return comparison data for different strategies
     */
    Map<String, Object> compareStrategies();
    
    /**
     * Check if user owns the strategy
     * @param strategyId the strategy ID
     * @return true if user owns the strategy
     */
    boolean userOwnsStrategy(Long strategyId);
} 