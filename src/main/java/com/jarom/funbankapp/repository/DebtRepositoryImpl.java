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

import com.jarom.funbankapp.model.Debt;

@Repository
public class DebtRepositoryImpl implements DebtRepository {

    private final JdbcTemplate jdbcTemplate;

    public DebtRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // RowMapper for Debt
    private final RowMapper<Debt> debtRowMapper = (rs, rowNum) -> {
        Debt debt = new Debt();
        debt.setId(rs.getLong("id"));
        debt.setUserId(rs.getLong("user_id"));
        debt.setName(rs.getString("name"));
        debt.setDebtType(rs.getString("debt_type"));
        debt.setCurrentBalance(rs.getBigDecimal("current_balance"));
        debt.setOriginalAmount(rs.getBigDecimal("original_amount"));
        debt.setInterestRate(rs.getBigDecimal("interest_rate"));
        debt.setMinimumPayment(rs.getBigDecimal("minimum_payment"));
        debt.setDueDate(rs.getInt("due_date"));
        debt.setPriority(rs.getString("priority"));
        debt.setNotes(rs.getString("notes"));
        
        // Handle nullable timestamp columns
        try {
            debt.setStartDate(rs.getTimestamp("start_date"));
        } catch (Exception e) {
            debt.setStartDate(null);
        }
        
        try {
            debt.setEndDate(rs.getTimestamp("end_date"));
        } catch (Exception e) {
            debt.setEndDate(null);
        }
        
        try {
            debt.setCreatedAt(rs.getTimestamp("created_at"));
        } catch (Exception e) {
            debt.setCreatedAt(null);
        }
        
        try {
            debt.setUpdatedAt(rs.getTimestamp("updated_at"));
        } catch (Exception e) {
            debt.setUpdatedAt(null);
        }
        
        return debt;
    };

    @Override
    public Debt save(Debt debt) {
        if (debt.getId() == null) {
            return createDebt(debt);
        } else {
            update(debt);
            return debt;
        }
    }

    private Debt createDebt(Debt debt) {
        String sql = "INSERT INTO debts (user_id, name, debt_type, current_balance, original_amount, " +
                    "interest_rate, minimum_payment, due_date, priority, notes, start_date, end_date, created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, debt.getUserId());
            ps.setString(2, debt.getName());
            ps.setString(3, debt.getDebtType());
            ps.setBigDecimal(4, debt.getCurrentBalance());
            ps.setBigDecimal(5, debt.getOriginalAmount());
            ps.setBigDecimal(6, debt.getInterestRate());
            ps.setBigDecimal(7, debt.getMinimumPayment());
            ps.setInt(8, debt.getDueDate());
            ps.setString(9, debt.getPriority());
            ps.setString(10, debt.getNotes());
            ps.setTimestamp(11, debt.getStartDate());
            ps.setTimestamp(12, debt.getEndDate());
            ps.setTimestamp(13, new Timestamp(System.currentTimeMillis()));
            ps.setTimestamp(14, new Timestamp(System.currentTimeMillis()));
            return ps;
        }, keyHolder);
        
        if (rowsAffected > 0 && keyHolder.getKey() != null) {
            Long id = keyHolder.getKey().longValue();
            debt.setId(id);
        }
        
        return debt;
    }

    @Override
    public Optional<Debt> findById(Long id) {
        String sql = "SELECT * FROM debts WHERE id = ?";
        List<Debt> debts = jdbcTemplate.query(sql, debtRowMapper, id);
        return debts.isEmpty() ? Optional.empty() : Optional.of(debts.get(0));
    }

    @Override
    public List<Debt> findByUserId(Long userId) {
        String sql = "SELECT * FROM debts WHERE user_id = ? ORDER BY priority DESC, current_balance DESC";
        return jdbcTemplate.query(sql, debtRowMapper, userId);
    }

    @Override
    public List<Debt> findAll() {
        String sql = "SELECT * FROM debts ORDER BY priority DESC, current_balance DESC";
        return jdbcTemplate.query(sql, debtRowMapper);
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM debts WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void update(Debt debt) {
        String sql = "UPDATE debts SET name = ?, debt_type = ?, current_balance = ?, original_amount = ?, " +
                    "interest_rate = ?, minimum_payment = ?, due_date = ?, priority = ?, notes = ?, " +
                    "start_date = ?, end_date = ?, updated_at = ? WHERE id = ?";
        
        jdbcTemplate.update(sql, 
            debt.getName(),
            debt.getDebtType(),
            debt.getCurrentBalance(),
            debt.getOriginalAmount(),
            debt.getInterestRate(),
            debt.getMinimumPayment(),
            debt.getDueDate(),
            debt.getPriority(),
            debt.getNotes(),
            debt.getStartDate(),
            debt.getEndDate(),
            new Timestamp(System.currentTimeMillis()),
            debt.getId());
    }

    public List<Debt> findByUserIdAndType(Long userId, String debtType) {
        String sql = "SELECT * FROM debts WHERE user_id = ? AND debt_type = ? ORDER BY priority DESC, current_balance DESC";
        return jdbcTemplate.query(sql, debtRowMapper, userId, debtType);
    }

    public List<Debt> findByUserIdAndPriority(Long userId, String priority) {
        String sql = "SELECT * FROM debts WHERE user_id = ? AND priority = ? ORDER BY current_balance DESC";
        return jdbcTemplate.query(sql, debtRowMapper, userId, priority);
    }

    public BigDecimal getTotalDebtAmount(Long userId) {
        String sql = "SELECT COALESCE(SUM(current_balance), 0) FROM debts WHERE user_id = ?";
        return jdbcTemplate.queryForObject(sql, BigDecimal.class, userId);
    }

    public BigDecimal getTotalMinimumPayments(Long userId) {
        String sql = "SELECT COALESCE(SUM(minimum_payment), 0) FROM debts WHERE user_id = ?";
        return jdbcTemplate.queryForObject(sql, BigDecimal.class, userId);
    }
} 