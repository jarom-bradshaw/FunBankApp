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
    private final RowMapper<Account> accountRowMapper = (rs, rowNum) -> {
        Account account = new Account();
        account.setId(rs.getLong("id"));
        account.setUserId(rs.getLong("user_id"));
        account.setAccountNumber(rs.getString("account_number"));
        account.setBalance(rs.getBigDecimal("balance"));
        account.setAccountType(rs.getString("account_type"));
        account.setName(rs.getString("name"));
        account.setColor(rs.getString("color"));
        account.setCreatedAt(rs.getTimestamp("created_at"));
        account.setUpdatedAt(rs.getTimestamp("updated_at"));
        return account;
    };


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
        List<Account> accounts = jdbcTemplate.query(sql, accountRowMapper, accountId);
        return accounts.isEmpty() ? null : accounts.get(0);
    }

    // Update account
    public void updateAccount(Account account) {
        String sql = "UPDATE accounts SET name = ?, color = ?, updated_at = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                account.getName(),
                account.getColor(),
                account.getUpdatedAt(),
                account.getId()
        );
    }

    // Delete account
    public void deleteAccount(Long accountId) {
        String sql = "DELETE FROM accounts WHERE id = ?";
        jdbcTemplate.update(sql, accountId);
    }

}
