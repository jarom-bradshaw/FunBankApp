package com.jarom.funbankapp.repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.jarom.funbankapp.model.DebtStrategy;

@Repository
public class DebtStrategyRepositoryImpl implements DebtStrategyRepository {

    private final JdbcTemplate jdbcTemplate;

    public DebtStrategyRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // RowMapper for DebtStrategy
    private final RowMapper<DebtStrategy> debtStrategyRowMapper = (rs, rowNum) -> {
        DebtStrategy strategy = new DebtStrategy();
        strategy.setId(rs.getLong("id"));
        strategy.setUserId(rs.getLong("user_id"));
        strategy.setName(rs.getString("name"));
        strategy.setStrategyType(rs.getString("strategy_type"));
        strategy.setMonthlyPaymentAmount(rs.getString("monthly_payment_amount"));
        strategy.setIsActive(rs.getBoolean("is_active"));
        strategy.setDescription(rs.getString("description"));
        
        // Handle nullable timestamp columns
        try {
            strategy.setCreatedAt(rs.getTimestamp("created_at"));
        } catch (Exception e) {
            strategy.setCreatedAt(null);
        }
        
        try {
            strategy.setUpdatedAt(rs.getTimestamp("updated_at"));
        } catch (Exception e) {
            strategy.setUpdatedAt(null);
        }
        
        return strategy;
    };

    @Override
    public DebtStrategy save(DebtStrategy strategy) {
        if (strategy.getId() == null) {
            return createStrategy(strategy);
        } else {
            update(strategy);
            return strategy;
        }
    }

    private DebtStrategy createStrategy(DebtStrategy strategy) {
        String sql = "INSERT INTO debt_strategies (user_id, name, strategy_type, monthly_payment_amount, " +
                    "is_active, description, created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, strategy.getUserId());
            ps.setString(2, strategy.getName());
            ps.setString(3, strategy.getStrategyType());
            ps.setString(4, strategy.getMonthlyPaymentAmount());
            ps.setBoolean(5, strategy.getIsActive());
            ps.setString(6, strategy.getDescription());
            ps.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
            ps.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
            return ps;
        }, keyHolder);
        
        if (rowsAffected > 0 && keyHolder.getKey() != null) {
            Long id = keyHolder.getKey().longValue();
            strategy.setId(id);
        }
        
        return strategy;
    }

    @Override
    public Optional<DebtStrategy> findById(Long id) {
        String sql = "SELECT * FROM debt_strategies WHERE id = ?";
        List<DebtStrategy> strategies = jdbcTemplate.query(sql, debtStrategyRowMapper, id);
        return strategies.isEmpty() ? Optional.empty() : Optional.of(strategies.get(0));
    }

    @Override
    public List<DebtStrategy> findByUserId(Long userId) {
        String sql = "SELECT * FROM debt_strategies WHERE user_id = ? ORDER BY is_active DESC, created_at DESC";
        return jdbcTemplate.query(sql, debtStrategyRowMapper, userId);
    }

    @Override
    public List<DebtStrategy> findAll() {
        String sql = "SELECT * FROM debt_strategies ORDER BY is_active DESC, created_at DESC";
        return jdbcTemplate.query(sql, debtStrategyRowMapper);
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM debt_strategies WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void update(DebtStrategy strategy) {
        String sql = "UPDATE debt_strategies SET user_id = ?, name = ?, strategy_type = ?, " +
                    "monthly_payment_amount = ?, is_active = ?, description = ?, updated_at = ? WHERE id = ?";
        
        jdbcTemplate.update(sql, 
            strategy.getUserId(),
            strategy.getName(),
            strategy.getStrategyType(),
            strategy.getMonthlyPaymentAmount(),
            strategy.getIsActive(),
            strategy.getDescription(),
            new Timestamp(System.currentTimeMillis()),
            strategy.getId());
    }

    public Optional<DebtStrategy> findActiveByUserId(Long userId) {
        String sql = "SELECT * FROM debt_strategies WHERE user_id = ? AND is_active = true";
        List<DebtStrategy> strategies = jdbcTemplate.query(sql, debtStrategyRowMapper, userId);
        return strategies.isEmpty() ? Optional.empty() : Optional.of(strategies.get(0));
    }

    public void deactivateAllByUserId(Long userId) {
        String sql = "UPDATE debt_strategies SET is_active = false, updated_at = ? WHERE user_id = ?";
        jdbcTemplate.update(sql, new Timestamp(System.currentTimeMillis()), userId);
    }

    public List<DebtStrategy> findByUserIdAndType(Long userId, String strategyType) {
        String sql = "SELECT * FROM debt_strategies WHERE user_id = ? AND strategy_type = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, debtStrategyRowMapper, userId, strategyType);
    }
} 