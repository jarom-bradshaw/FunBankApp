package com.jarom.funbankapp.repository;

import com.jarom.funbankapp.model.DebtPayment;
import java.util.List;
import java.util.Optional;

public interface DebtPaymentRepository {
    DebtPayment save(DebtPayment payment);
    Optional<DebtPayment> findById(Long id);
    List<DebtPayment> findByDebtId(Long debtId);
    List<DebtPayment> findAll();
    void deleteById(Long id);
    void update(DebtPayment payment);
} 