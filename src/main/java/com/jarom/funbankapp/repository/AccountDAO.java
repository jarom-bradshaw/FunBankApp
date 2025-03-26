package com.jarom.funbankapp.repository;

import com.jarom.funbankapp.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class AccountDAO {

    private final JdbcTemplate jdbcTemplate;

    public AccountDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // RowMapper for Account
    private final RowMapper<Account> accountRowMapper = (rs, rowNum) -> new Account(
            rs.getInt("id"),
            rs.getInt("user_id"),
            rs.getString("account_number"),
            rs.getBigDecimal("balance"),
            rs.getString("account_type"),
            rs.getTimestamp("created_at")
    );


    // Create new account
    public int createAccount(Account account) {
        String sql = "INSERT INTO accounts (user_id, account_number, balance, account_type) VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                account.getUserId(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getAccountType()
        );
    }

    // Get all accounts for a user
    public List<Account> findByUserId(int userId) {
        String sql = "SELECT * FROM accounts WHERE user_id = ?";
        return jdbcTemplate.query(sql, accountRowMapper, userId);
    }

    // Get account balance by account ID
    public BigDecimal getBalance(int accountId) {
        String sql = "SELECT balance FROM accounts WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, BigDecimal.class, accountId);
    }
}
