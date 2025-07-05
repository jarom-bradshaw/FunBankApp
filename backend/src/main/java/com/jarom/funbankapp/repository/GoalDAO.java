package com.jarom.funbankapp.repository;

import com.jarom.funbankapp.model.Goal;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class GoalDAO {

    private final JdbcTemplate jdbcTemplate;

    public GoalDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Goal> goalRowMapper = (rs, rowNum) -> {
        Goal goal = new Goal();
        goal.setId(rs.getLong("id"));
        goal.setUserId(rs.getLong("user_id"));
        goal.setName(rs.getString("name"));
        goal.setTargetAmount(rs.getBigDecimal("target_amount"));
        goal.setCurrentAmount(rs.getBigDecimal("current_amount"));
        goal.setDeadline(rs.getTimestamp("deadline"));
        goal.setType(rs.getString("type"));
        goal.setStatus(rs.getString("status"));
        goal.setCreatedAt(rs.getTimestamp("created_at"));
        goal.setUpdatedAt(rs.getTimestamp("updated_at"));
        return goal;
    };

    public void createGoal(Goal goal) {
        String sql = "INSERT INTO goals (user_id, name, target_amount, current_amount, deadline, type, status, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                goal.getUserId(),
                goal.getName(),
                goal.getTargetAmount(),
                goal.getCurrentAmount(),
                goal.getDeadline(),
                goal.getType(),
                goal.getStatus(),
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis())
        );
    }

    public List<Goal> findByUserId(Long userId) {
        String sql = "SELECT * FROM goals WHERE user_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, goalRowMapper, userId);
    }

    public Goal findById(Long id) {
        String sql = "SELECT * FROM goals WHERE id = ?";
        List<Goal> goals = jdbcTemplate.query(sql, goalRowMapper, id);
        return goals.isEmpty() ? null : goals.get(0);
    }

    public void updateGoal(Goal goal) {
        String sql = "UPDATE goals SET name = ?, target_amount = ?, current_amount = ?, deadline = ?, type = ?, status = ?, updated_at = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                goal.getName(),
                goal.getTargetAmount(),
                goal.getCurrentAmount(),
                goal.getDeadline(),
                goal.getType(),
                goal.getStatus(),
                new Timestamp(System.currentTimeMillis()),
                goal.getId()
        );
    }

    public void deleteGoal(Long id) {
        String sql = "DELETE FROM goals WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public void updateProgress(Long goalId, BigDecimal currentAmount) {
        String sql = "UPDATE goals SET current_amount = ?, updated_at = ? WHERE id = ?";
        jdbcTemplate.update(sql, currentAmount, new Timestamp(System.currentTimeMillis()), goalId);
    }
} 