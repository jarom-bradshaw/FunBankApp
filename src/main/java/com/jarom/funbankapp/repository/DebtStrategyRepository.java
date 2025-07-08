package com.jarom.funbankapp.repository;

import com.jarom.funbankapp.model.DebtStrategy;
import java.util.List;
import java.util.Optional;

public interface DebtStrategyRepository {
    DebtStrategy save(DebtStrategy strategy);
    Optional<DebtStrategy> findById(Long id);
    List<DebtStrategy> findByUserId(Long userId);
    List<DebtStrategy> findAll();
    void deleteById(Long id);
    void update(DebtStrategy strategy);
} 