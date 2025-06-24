package com.jarom.funbankapp.repository;

import com.jarom.funbankapp.model.Account;
import com.jarom.funbankapp.model.User;
import java.util.List;

public interface AccountRepository {
    List<Account> findByUser(User user);
    List<Account> findByUserAndType(User user, String type);
    Double getTotalBalanceByUser(User user);
} 