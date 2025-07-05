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
            rs.getLong("id"),
            rs.getLong("user_id"),
            rs.getString("account_number"),
            rs.getString("name"),
            rs.getString("color"),
            rs.getBigDecimal("balance"),
            rs.getString("account_type"),
            rs.getTimestamp("created_at"),
            rs.getTimestamp("updated_at")
    );


    // Create new account
    public Long createAccount(Account account) {
        String sql = "INSERT INTO accounts (user_id, account_number, name, color, balance, account_type) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                account.getUserId(),
                account.getAccountNumber(),
                account.getName(),
                account.getColor(),
                account.getBalance(),
                account.getAccountType()
        );
        return jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
    }

    // Get all accounts for a user
    public List<Account> findByUserId(Long userId) {
        String sql = "SELECT * FROM accounts WHERE user_id = ?";
        return jdbcTemplate.query(sql, accountRowMapper, userId);
    }

    // Get account balance by account ID
    public BigDecimal getBalance(Long accountId) {
        String sql = "SELECT balance FROM accounts WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, BigDecimal.class, accountId);
    }

    public void updateBalance(Long accountId, BigDecimal newBalance) {
        String sql = "UPDATE accounts SET balance = ? WHERE id = ?";
        jdbcTemplate.update(sql, newBalance, accountId);
    }

    // Find account by ID
    public Account findById(Long accountId) {
        String sql = "SELECT * FROM accounts WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, accountRowMapper, accountId);
    }

    // Update account
    public void updateAccount(Account account) {
        String sql = "UPDATE accounts SET name = ?, color = ?, updated_at = ? WHERE id = ?";
        jdbcTemplate.update(sql, account.getName(), account.getColor(), account.getUpdatedAt(), account.getId());
    }

    // Delete account
    public void deleteAccount(Long accountId) {
        String sql = "DELETE FROM accounts WHERE id = ?";
        jdbcTemplate.update(sql, accountId);
    }

}
