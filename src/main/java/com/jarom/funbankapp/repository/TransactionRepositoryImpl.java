package com.jarom.funbankapp.repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.jarom.funbankapp.model.Transaction;

@Repository
public class TransactionRepositoryImpl implements TransactionRepository {

    private final JdbcTemplate jdbcTemplate;

    public TransactionRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Transaction> transactionRowMapper = (rs, rowNum) -> {
        Transaction transaction = new Transaction();
        transaction.setId(rs.getLong("id"));
        transaction.setAccountId(rs.getLong("account_id"));
        transaction.setType(rs.getString("type"));
        transaction.setAmount(rs.getBigDecimal("amount"));
        transaction.setCategory(rs.getString("category"));
        transaction.setDescription(rs.getString("description"));
        transaction.setTransactionDate(rs.getTimestamp("transaction_date"));
        transaction.setCreatedAt(rs.getTimestamp("created_at"));
        return transaction;
    };

    @Override
    public int logTransaction(Long accountId, String type, BigDecimal amount, String description) {
        String sql = "INSERT INTO transactions (account_id, type, amount, description, transaction_date, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, accountId, type, amount, description, new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));
    }

    @Override
    public int logTransaction(Long accountId, String type, BigDecimal amount, String category, String description) {
        String sql = "INSERT INTO transactions (account_id, type, amount, category, description, transaction_date, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, accountId, type, amount, category, description, new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));
    }

    public int logTransaction(Long accountId, String type, BigDecimal amount, String category, String description, Timestamp transactionDate) {
        String sql = "INSERT INTO transactions (account_id, type, amount, category, description, transaction_date, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, accountId, type, amount, category, description, transactionDate, new Timestamp(System.currentTimeMillis()));
    }

    @Override
    public List<Transaction> findByAccountId(Long accountId) {
        String sql = "SELECT * FROM transactions WHERE account_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, transactionRowMapper, accountId);
    }

    @Override
    public List<Transaction> getRecentTransactions(Long userId, int limit) {
        String sql = "SELECT t.* FROM transactions t " +
                    "JOIN accounts a ON t.account_id = a.id " +
                    "WHERE a.user_id = ? " +
                    "ORDER BY t.created_at DESC " +
                    "LIMIT ?";
        return jdbcTemplate.query(sql, transactionRowMapper, userId, limit);
    }

    @Override
    public Map<String, BigDecimal> getSpendingByCategory(Long userId, int days) {
        String sql = "SELECT COALESCE(t.category, t.description) as category, SUM(t.amount) as total " +
                    "FROM transactions t " +
                    "JOIN accounts a ON t.account_id = a.id " +
                    "WHERE a.user_id = ? " +
                    "AND t.type = 'withdraw' " +
                    "AND t.created_at >= DATE_SUB(NOW(), INTERVAL ? DAY) " +
                    "GROUP BY COALESCE(t.category, t.description) " +
                    "ORDER BY total DESC";
        
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, userId, days);
        
        return results.stream()
                .collect(Collectors.toMap(
                    row -> (String) row.get("category"),
                    row -> (BigDecimal) row.get("total")
                ));
    }

    @Override
    public Map<String, BigDecimal> getIncomeByCategory(Long userId, int days) {
        String sql = "SELECT COALESCE(t.category, t.description) as category, SUM(t.amount) as total " +
                    "FROM transactions t " +
                    "JOIN accounts a ON t.account_id = a.id " +
                    "WHERE a.user_id = ? " +
                    "AND t.type = 'deposit' " +
                    "AND t.created_at >= DATE_SUB(NOW(), INTERVAL ? DAY) " +
                    "GROUP BY COALESCE(t.category, t.description) " +
                    "ORDER BY total DESC";
        
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, userId, days);
        
        return results.stream()
                .collect(Collectors.toMap(
                    row -> (String) row.get("category"),
                    row -> (BigDecimal) row.get("total")
                ));
    }

    @Override
    public Map<String, BigDecimal> getSpendingByMonth(Long userId, int months) {
        String sql = "SELECT DATE_FORMAT(t.created_at, '%Y-%m') as month, SUM(t.amount) as total " +
                    "FROM transactions t " +
                    "JOIN accounts a ON t.account_id = a.id " +
                    "WHERE a.user_id = ? " +
                    "AND t.type = 'withdraw' " +
                    "AND t.created_at >= DATE_SUB(NOW(), INTERVAL ? MONTH) " +
                    "GROUP BY DATE_FORMAT(t.created_at, '%Y-%m') " +
                    "ORDER BY month DESC";
        
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, userId, months);
        
        return results.stream()
                .collect(Collectors.toMap(
                    row -> (String) row.get("month"),
                    row -> (BigDecimal) row.get("total")
                ));
    }

    @Override
    public Long getTransactionCount(Long userId, int days) {
        String sql = "SELECT COUNT(*) as count " +
                    "FROM transactions t " +
                    "JOIN accounts a ON t.account_id = a.id " +
                    "WHERE a.user_id = ? " +
                    "AND t.created_at >= DATE_SUB(NOW(), INTERVAL ? DAY)";
        
        return jdbcTemplate.queryForObject(sql, Long.class, userId, days);
    }

    @Override
    public Long getTransactionCountByType(Long userId, String type, int days) {
        String sql = "SELECT COUNT(*) as count " +
                    "FROM transactions t " +
                    "JOIN accounts a ON t.account_id = a.id " +
                    "WHERE a.user_id = ? " +
                    "AND t.type = ? " +
                    "AND t.created_at >= DATE_SUB(NOW(), INTERVAL ? DAY)";
        
        return jdbcTemplate.queryForObject(sql, Long.class, userId, type, days);
    }

    @Override
    public Optional<Transaction> findById(Long transactionId) {
        String sql = "SELECT * FROM transactions WHERE id = ?";
        List<Transaction> results = jdbcTemplate.query(sql, transactionRowMapper, transactionId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public Transaction save(Transaction transaction) {
        if (transaction.getId() == null) {
            // Insert new transaction
            String sql = "INSERT INTO transactions (account_id, type, amount, category, description, transaction_date, created_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql, 
                transaction.getAccountId(), 
                transaction.getType(), 
                transaction.getAmount(), 
                transaction.getCategory(), 
                transaction.getDescription(), 
                transaction.getTransactionDate(), 
                new Timestamp(System.currentTimeMillis())
            );
            
            // Get the generated ID
            String idSql = "SELECT LAST_INSERT_ID()";
            Long id = jdbcTemplate.queryForObject(idSql, Long.class);
            transaction.setId(id);
            transaction.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        } else {
            // Update existing transaction
            String sql = "UPDATE transactions SET account_id = ?, type = ?, amount = ?, category = ?, " +
                        "description = ?, transaction_date = ? WHERE id = ?";
            jdbcTemplate.update(sql, 
                transaction.getAccountId(), 
                transaction.getType(), 
                transaction.getAmount(), 
                transaction.getCategory(), 
                transaction.getDescription(), 
                transaction.getTransactionDate(), 
                transaction.getId()
            );
        }
        return transaction;
    }

    @Override
    public boolean isAccountOwnedByUser(Long accountId, Long userId) {
        String sql = "SELECT COUNT(*) FROM accounts WHERE id = ? AND user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, accountId, userId);
        return count != null && count > 0;
    }

    @Override
    public void deleteById(Long transactionId) {
        String sql = "DELETE FROM transactions WHERE id = ?";
        jdbcTemplate.update(sql, transactionId);
    }

    @Override
    public int deleteByAccountId(Long accountId) {
        String sql = "DELETE FROM transactions WHERE account_id = ?";
        return jdbcTemplate.update(sql, accountId);
    }

    @Override
    public Long getTotalTransactionsInMonths(Long userId, int months) {
        String sql = "SELECT COUNT(*) as count " +
                    "FROM transactions t " +
                    "JOIN accounts a ON t.account_id = a.id " +
                    "WHERE a.user_id = ? " +
                    "AND t.created_at >= DATE_SUB(NOW(), INTERVAL ? MONTH)";
        
        return jdbcTemplate.queryForObject(sql, Long.class, userId, months);
    }

    @Override
    public BigDecimal getTotalSpendingInMonths(Long userId, int months) {
        String sql = "SELECT COALESCE(SUM(t.amount), 0) as total " +
                    "FROM transactions t " +
                    "JOIN accounts a ON t.account_id = a.id " +
                    "WHERE a.user_id = ? " +
                    "AND t.type = 'withdraw' " +
                    "AND t.created_at >= DATE_SUB(NOW(), INTERVAL ? MONTH)";
        
        return jdbcTemplate.queryForObject(sql, BigDecimal.class, userId, months);
    }

    @Override
    public BigDecimal getAverageTransactionAmountInMonths(Long userId, int months) {
        String sql = "SELECT COALESCE(AVG(t.amount), 0) as average " +
                    "FROM transactions t " +
                    "JOIN accounts a ON t.account_id = a.id " +
                    "WHERE a.user_id = ? " +
                    "AND t.created_at >= DATE_SUB(NOW(), INTERVAL ? MONTH)";
        
        return jdbcTemplate.queryForObject(sql, BigDecimal.class, userId, months);
    }

    @Override
    public Map<String, BigDecimal> getMonthlySpendingByYear(Long userId, int year) {
        String sql = "SELECT DATE_FORMAT(t.created_at, '%Y-%m') as month, COALESCE(SUM(t.amount), 0) as total " +
                    "FROM transactions t " +
                    "JOIN accounts a ON t.account_id = a.id " +
                    "WHERE a.user_id = ? " +
                    "AND t.type = 'withdraw' " +
                    "AND YEAR(t.created_at) = ? " +
                    "GROUP BY DATE_FORMAT(t.created_at, '%Y-%m') " +
                    "ORDER BY month";
        
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, userId, year);
        
        return results.stream()
                .collect(Collectors.toMap(
                    row -> (String) row.get("month"),
                    row -> (BigDecimal) row.get("total")
                ));
    }

    @Override
    public Map<String, Long> getMonthlyTransactionCountByYear(Long userId, int year) {
        String sql = "SELECT DATE_FORMAT(t.created_at, '%Y-%m') as month, COUNT(*) as count " +
                    "FROM transactions t " +
                    "JOIN accounts a ON t.account_id = a.id " +
                    "WHERE a.user_id = ? " +
                    "AND YEAR(t.created_at) = ? " +
                    "GROUP BY DATE_FORMAT(t.created_at, '%Y-%m') " +
                    "ORDER BY month";
        
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, userId, year);
        
        return results.stream()
                .collect(Collectors.toMap(
                    row -> (String) row.get("month"),
                    row -> (Long) row.get("count")
                ));
    }

    @Override
    public Map<String, BigDecimal> getTopCategoriesByYear(Long userId, int year) {
        String sql = "SELECT COALESCE(t.category, t.description) as category, SUM(t.amount) as total " +
                    "FROM transactions t " +
                    "JOIN accounts a ON t.account_id = a.id " +
                    "WHERE a.user_id = ? " +
                    "AND t.type = 'withdraw' " +
                    "AND YEAR(t.created_at) = ? " +
                    "GROUP BY COALESCE(t.category, t.description) " +
                    "ORDER BY total DESC " +
                    "LIMIT 10";
        
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, userId, year);
        
        return results.stream()
                .collect(Collectors.toMap(
                    row -> (String) row.get("category"),
                    row -> (BigDecimal) row.get("total")
                ));
    }
} 