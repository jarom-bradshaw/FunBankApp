package com.jarom.funbankapp.repository;

import com.jarom.funbankapp.model.DebtReminder;
import java.util.List;
import java.util.Optional;

public interface DebtReminderRepository {
    DebtReminder save(DebtReminder reminder);
    Optional<DebtReminder> findById(Long id);
    List<DebtReminder> findByDebtId(Long debtId);
    List<DebtReminder> findAll();
    void deleteById(Long id);
    void update(DebtReminder reminder);
} 