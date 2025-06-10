package com.jarom.funbankapp.repository;

import com.jarom.funbankapp.model.Transaction;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class TransactionDAO {

    private final JdbcTemplate jdbcTemplate;

    public TransactionDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Transaction> transactionRowMapper = (rs, rowNum) -> new Transaction(
            rs.getInt("id"),
            rs.getInt("account_id"),
            rs.getString("type"),
            rs.getBigDecimal("amount"),
            rs.getString("description"),
            rs.getTimestamp("created_at")
    );

    // Create a new transaction record
    public int logTransaction(int accountId, String type, BigDecimal amount, String description) {
        String sql = "INSERT INTO transactions (account_id, type, amount, description) VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(sql, accountId, type, amount, description);
    }

    // Get all transactions for an account
    public List<Transaction> findByAccountId(int accountId) {
        String sql = "SELECT * FROM transactions WHERE account_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, transactionRowMapper, accountId);
    }
}
