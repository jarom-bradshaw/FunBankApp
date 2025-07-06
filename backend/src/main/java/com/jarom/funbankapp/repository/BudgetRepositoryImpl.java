package com.jarom.funbankapp.repository;

import com.jarom.funbankapp.model.Budget;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class BudgetRepositoryImpl implements BudgetRepository {

    private final JdbcTemplate jdbcTemplate;

    public BudgetRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Budget> budgetRowMapper = (rs, rowNum) -> {
        Budget budget = new Budget();
        budget.setId(rs.getLong("id"));
        budget.setUserId(rs.getLong("user_id"));
        budget.setName(rs.getString("name"));
        budget.setCategory(rs.getString("category"));
        budget.setAmount(rs.getBigDecimal("amount"));
        budget.setSpent(rs.getBigDecimal("spent"));
        budget.setPeriod(rs.getString("period"));
        budget.setStartDate(rs.getTimestamp("start_date"));
        budget.setEndDate(rs.getTimestamp("end_date"));
        budget.setCreatedAt(rs.getTimestamp("created_at"));
        budget.setUpdatedAt(rs.getTimestamp("updated_at"));
        return budget;
    };

    @Override
    public void createBudget(Budget budget) {
        String sql = "INSERT INTO budgets (user_id, name, category, amount, period, start_date, end_date, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                budget.getUserId(),
                budget.getName(),
                budget.getCategory(),
                budget.getAmount(),
                budget.getPeriod(),
                budget.getStartDate(),
                budget.getEndDate(),
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis())
        );
    }

    @Override
    public Optional<Budget> findById(Long id) {
        String sql = "SELECT * FROM budgets WHERE id = ?";
        List<Budget> results = jdbcTemplate.query(sql, budgetRowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<Budget> findByUserId(Long userId) {
        String sql = "SELECT * FROM budgets WHERE user_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, budgetRowMapper, userId);
    }

    @Override
    public void updateBudget(Budget budget) {
        String sql = "UPDATE budgets SET name = ?, category = ?, amount = ?, period = ?, start_date = ?, end_date = ?, updated_at = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                budget.getName(),
                budget.getCategory(),
                budget.getAmount(),
                budget.getPeriod(),
                budget.getStartDate(),
                budget.getEndDate(),
                new Timestamp(System.currentTimeMillis()),
                budget.getId()
        );
    }

    @Override
    public void deleteBudget(Long id) {
        String sql = "DELETE FROM budgets WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void updateSpent(Long budgetId, BigDecimal spent) {
        String sql = "UPDATE budgets SET spent = ?, updated_at = ? WHERE id = ?";
        jdbcTemplate.update(sql, spent, new Timestamp(System.currentTimeMillis()), budgetId);
    }
} 