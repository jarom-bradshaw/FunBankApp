package com.jarom.funbankapp.repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.jarom.funbankapp.model.Account;
import com.jarom.funbankapp.model.User;

@Repository
public class AccountRepositoryImpl implements AccountRepository {

    private final JdbcTemplate jdbcTemplate;

    public AccountRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // RowMapper for Account
    private final RowMapper<Account> accountRowMapper = (rs, rowNum) -> {
        Account account = new Account();
        account.setId(rs.getLong("id"));
        account.setUserId(rs.getLong("user_id"));
        account.setAccountNumber(rs.getString("account_number"));
        account.setBalance(rs.getBigDecimal("balance"));
        account.setAccountType(rs.getString("account_type")); // updated column name
        account.setName(rs.getString("name"));
        account.setColor(rs.getString("color")); // new column
        
        // Handle nullable timestamp columns
        try {
            account.setCreatedAt(rs.getTimestamp("created_at"));
        } catch (Exception e) {
            account.setCreatedAt(null);
        }
        
        try {
            account.setUpdatedAt(rs.getTimestamp("updated_at"));
        } catch (Exception e) {
            account.setUpdatedAt(null);
        }
        
        return account;
    };

    @Override
    public Account createAccount(Account account) {
        String sql = "INSERT INTO accounts (user_id, name, account_type, color, balance) VALUES (?, ?, ?, ?, ?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, account.getUserId());
            ps.setString(2, account.getName());
            ps.setString(3, account.getAccountType());
            ps.setString(4, account.getColor());
            ps.setBigDecimal(5, account.getBalance());
            return ps;
        }, keyHolder);
        
        if (rowsAffected > 0 && keyHolder.getKey() != null) {
            Long id = keyHolder.getKey().longValue();
            account.setId(id);
        }
        
        return account;
    }

    @Override
    public List<Account> findByUserId(Long userId) {
        String sql = "SELECT * FROM accounts WHERE user_id = ?";
        return jdbcTemplate.query(sql, accountRowMapper, userId);
    }

    @Override
    public Optional<Account> findById(Long accountId) {
        String sql = "SELECT * FROM accounts WHERE id = ?";
        List<Account> accounts = jdbcTemplate.query(sql, accountRowMapper, accountId);
        return accounts.isEmpty() ? Optional.empty() : Optional.of(accounts.get(0));
    }

    @Override
    public BigDecimal getBalance(Long accountId) {
        String sql = "SELECT balance FROM accounts WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, BigDecimal.class, accountId);
    }

    @Override
    public void updateBalance(Long accountId, BigDecimal newBalance) {
        String sql = "UPDATE accounts SET balance = ?, updated_at = NOW() WHERE id = ?";
        jdbcTemplate.update(sql, newBalance, accountId);
    }

    @Override
    public void updateAccount(Account account) {
        String sql = "UPDATE accounts SET name = ?, account_type = ?, color = ?, balance = ?, updated_at = NOW() WHERE id = ?";
        jdbcTemplate.update(sql, 
            account.getName(), 
            account.getAccountType(), 
            account.getColor(),
            account.getBalance(), 
            account.getId());
    }

    @Override
    public void deleteAccount(Long accountId) {
        String sql = "DELETE FROM accounts WHERE id = ?";
        jdbcTemplate.update(sql, accountId);
    }

    // Legacy methods for backward compatibility
    @Override
    public List<Account> findByUser(User user) {
        return findByUserId(user.getId());
    }

    @Override
    public List<Account> findByUserAndType(User user, String type) {
        String sql = "SELECT * FROM accounts WHERE user_id = ? AND account_type = ?";
        return jdbcTemplate.query(sql, accountRowMapper, user.getId(), type);
    }

    @Override
    public Double getTotalBalanceByUser(User user) {
        String sql = "SELECT COALESCE(SUM(balance), 0) FROM accounts WHERE user_id = ?";
        BigDecimal total = jdbcTemplate.queryForObject(sql, BigDecimal.class, user.getId());
        return total != null ? total.doubleValue() : 0.0;
    }
} 