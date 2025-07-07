package com.jarom.funbankapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import com.jarom.funbankapp.repository.AccountRepository;
import com.jarom.funbankapp.repository.AccountRepositoryImpl;
import com.jarom.funbankapp.repository.BudgetRepository;
import com.jarom.funbankapp.repository.BudgetRepositoryImpl;
import com.jarom.funbankapp.repository.CategoryRepository;
import com.jarom.funbankapp.repository.CategoryRepositoryImpl;
import com.jarom.funbankapp.repository.GoalRepository;
import com.jarom.funbankapp.repository.GoalRepositoryImpl;
import com.jarom.funbankapp.repository.TransactionRepository;
import com.jarom.funbankapp.repository.TransactionRepositoryImpl;
import com.jarom.funbankapp.repository.UserRepository;
import com.jarom.funbankapp.repository.UserRepositoryImpl;

@Configuration
public class RepositoryConfig {

    @Bean
    public AccountRepository accountRepository(JdbcTemplate jdbcTemplate) {
        return new AccountRepositoryImpl(jdbcTemplate);
    }

    @Bean
    public TransactionRepository transactionRepository(JdbcTemplate jdbcTemplate) {
        return new TransactionRepositoryImpl(jdbcTemplate);
    }

    @Bean
    public UserRepository userRepository(JdbcTemplate jdbcTemplate) {
        return new UserRepositoryImpl(jdbcTemplate);
    }

    @Bean
    public BudgetRepository budgetRepository(JdbcTemplate jdbcTemplate) {
        return new BudgetRepositoryImpl(jdbcTemplate);
    }

    @Bean
    public GoalRepository goalRepository(JdbcTemplate jdbcTemplate) {
        return new GoalRepositoryImpl(jdbcTemplate);
    }

    @Bean
    public CategoryRepository categoryRepository(JdbcTemplate jdbcTemplate) {
        return new CategoryRepositoryImpl(jdbcTemplate);
    }
} 