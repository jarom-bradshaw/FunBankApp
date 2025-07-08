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

import com.jarom.funbankapp.model.DebtPayment;

@Repository
public class DebtPaymentRepositoryImpl implements DebtPaymentRepository {

    private final JdbcTemplate jdbcTemplate;

    public DebtPaymentRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // RowMapper for DebtPayment
    private final RowMapper<DebtPayment> debtPaymentRowMapper = (rs, rowNum) -> {
        DebtPayment payment = new DebtPayment();
        payment.setId(rs.getLong("id"));
        payment.setDebtId(rs.getLong("debt_id"));
        payment.setAmount(rs.getBigDecimal("amount"));
        payment.setPaymentDate(rs.getTimestamp("payment_date"));
        payment.setPaymentType(rs.getString("payment_type"));
        payment.setNotes(rs.getString("notes"));
        
        // Handle nullable timestamp columns
        try {
            payment.setCreatedAt(rs.getTimestamp("created_at"));
        } catch (Exception e) {
            payment.setCreatedAt(null);
        }
        
        try {
            payment.setUpdatedAt(rs.getTimestamp("updated_at"));
        } catch (Exception e) {
            payment.setUpdatedAt(null);
        }
        
        return payment;
    };

    @Override
    public DebtPayment save(DebtPayment payment) {
        if (payment.getId() == null) {
            return createPayment(payment);
        } else {
            update(payment);
            return payment;
        }
    }

    private DebtPayment createPayment(DebtPayment payment) {
        String sql = "INSERT INTO debt_payments (debt_id, amount, payment_date, payment_type, notes, created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, payment.getDebtId());
            ps.setBigDecimal(2, payment.getAmount());
            ps.setTimestamp(3, payment.getPaymentDate());
            ps.setString(4, payment.getPaymentType());
            ps.setString(5, payment.getNotes());
            ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            ps.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
            return ps;
        }, keyHolder);
        
        if (rowsAffected > 0 && keyHolder.getKey() != null) {
            Long id = keyHolder.getKey().longValue();
            payment.setId(id);
        }
        
        return payment;
    }

    @Override
    public Optional<DebtPayment> findById(Long id) {
        String sql = "SELECT * FROM debt_payments WHERE id = ?";
        List<DebtPayment> payments = jdbcTemplate.query(sql, debtPaymentRowMapper, id);
        return payments.isEmpty() ? Optional.empty() : Optional.of(payments.get(0));
    }

    @Override
    public List<DebtPayment> findByDebtId(Long debtId) {
        String sql = "SELECT * FROM debt_payments WHERE debt_id = ? ORDER BY payment_date DESC";
        return jdbcTemplate.query(sql, debtPaymentRowMapper, debtId);
    }

    @Override
    public List<DebtPayment> findAll() {
        String sql = "SELECT * FROM debt_payments ORDER BY payment_date DESC";
        return jdbcTemplate.query(sql, debtPaymentRowMapper);
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM debt_payments WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void update(DebtPayment payment) {
        String sql = "UPDATE debt_payments SET debt_id = ?, amount = ?, payment_date = ?, " +
                    "payment_type = ?, notes = ?, updated_at = ? WHERE id = ?";
        
        jdbcTemplate.update(sql, 
            payment.getDebtId(),
            payment.getAmount(),
            payment.getPaymentDate(),
            payment.getPaymentType(),
            payment.getNotes(),
            new Timestamp(System.currentTimeMillis()),
            payment.getId());
    }

    public BigDecimal getTotalPaymentsForDebt(Long debtId) {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM debt_payments WHERE debt_id = ?";
        return jdbcTemplate.queryForObject(sql, BigDecimal.class, debtId);
    }

    public List<DebtPayment> findByDebtIdAndDateRange(Long debtId, Timestamp startDate, Timestamp endDate) {
        String sql = "SELECT * FROM debt_payments WHERE debt_id = ? AND payment_date BETWEEN ? AND ? ORDER BY payment_date DESC";
        return jdbcTemplate.query(sql, debtPaymentRowMapper, debtId, startDate, endDate);
    }
} 