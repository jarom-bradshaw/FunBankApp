package com.jarom.funbankapp.repository;

import com.jarom.funbankapp.model.Transaction;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class TransactionDAO {

    private final JdbcTemplate jdbcTemplate;

    public TransactionDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Transaction> transactionRowMapper = (rs, rowNum) -> {
        Transaction transaction = new Transaction();
        transaction.setId(rs.getLong("id"));
        transaction.setAccountId(rs.getLong("account_id"));
        transaction.setType(rs.getString("type"));
        transaction.setAmount(rs.getBigDecimal("amount"));
        transaction.setDescription(rs.getString("description"));
        transaction.setCreatedAt(rs.getTimestamp("created_at"));
        return transaction;
    };

    // Create a new transaction record
    public int logTransaction(Long accountId, String type, BigDecimal amount, String description) {
        String sql = "INSERT INTO transactions (account_id, type, amount, description) VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(sql, accountId, type, amount, description);
    }

    // Get all transactions for an account
    public List<Transaction> findByAccountId(Long accountId) {
        String sql = "SELECT * FROM transactions WHERE account_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, transactionRowMapper, accountId);
    }

    // Get recent transactions for a user (across all their accounts)
    public List<Transaction> getRecentTransactions(Long userId, int limit) {
        String sql = "SELECT t.* FROM transactions t " +
                    "JOIN accounts a ON t.account_id = a.id " +
                    "WHERE a.user_id = ? " +
                    "ORDER BY t.created_at DESC " +
                    "LIMIT ?";
        return jdbcTemplate.query(sql, transactionRowMapper, userId, limit);
    }

    // Get spending by category for a user (across all their accounts)
    public Map<String, BigDecimal> getSpendingByCategory(Long userId, int days) {
        String sql = "SELECT t.description as category, SUM(t.amount) as total " +
                    "FROM transactions t " +
                    "JOIN accounts a ON t.account_id = a.id " +
                    "WHERE a.user_id = ? " +
                    "AND t.type = 'withdraw' " +
                    "AND t.created_at >= DATE_SUB(NOW(), INTERVAL ? DAY) " +
                    "GROUP BY t.description " +
                    "ORDER BY total DESC";
        
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, userId, days);
        
        return results.stream()
                .collect(Collectors.toMap(
                    row -> (String) row.get("category"),
                    row -> (BigDecimal) row.get("total")
                ));
    }
}
