package com.jarom.funbankapp.repository;

import com.jarom.funbankapp.model.Debt;
import java.util.List;
import java.util.Optional;

public interface DebtRepository {
    Debt save(Debt debt);
    Optional<Debt> findById(Long id);
    List<Debt> findByUserId(Long userId);
    List<Debt> findAll();
    void deleteById(Long id);
    void update(Debt debt);
} 