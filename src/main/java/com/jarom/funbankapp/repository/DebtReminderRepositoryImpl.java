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

import com.jarom.funbankapp.model.DebtReminder;

@Repository
public class DebtReminderRepositoryImpl implements DebtReminderRepository {

    private final JdbcTemplate jdbcTemplate;

    public DebtReminderRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // RowMapper for DebtReminder
    private final RowMapper<DebtReminder> debtReminderRowMapper = (rs, rowNum) -> {
        DebtReminder reminder = new DebtReminder();
        reminder.setId(rs.getLong("id"));
        reminder.setDebtId(rs.getLong("debt_id"));
        reminder.setReminderDate(rs.getTimestamp("reminder_date"));
        reminder.setDueDate(rs.getTimestamp("due_date"));
        reminder.setAmount(rs.getBigDecimal("amount"));
        reminder.setReminderType(rs.getString("reminder_type"));
        reminder.setIsSent(rs.getBoolean("is_sent"));
        reminder.setIsActive(rs.getBoolean("is_active"));
        reminder.setSentDate(rs.getTimestamp("sent_date"));
        reminder.setNotes(rs.getString("notes"));
        
        // Handle nullable timestamp columns
        try {
            reminder.setCreatedAt(rs.getTimestamp("created_at"));
        } catch (Exception e) {
            reminder.setCreatedAt(null);
        }
        
        try {
            reminder.setUpdatedAt(rs.getTimestamp("updated_at"));
        } catch (Exception e) {
            reminder.setUpdatedAt(null);
        }
        
        return reminder;
    };

    @Override
    public DebtReminder save(DebtReminder reminder) {
        if (reminder.getId() == null) {
            return createReminder(reminder);
        } else {
            update(reminder);
            return reminder;
        }
    }

    private DebtReminder createReminder(DebtReminder reminder) {
        String sql = "INSERT INTO debt_reminders (debt_id, reminder_date, due_date, amount, reminder_type, " +
                    "is_sent, is_active, sent_date, notes, created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, reminder.getDebtId());
            ps.setTimestamp(2, reminder.getReminderDate());
            ps.setTimestamp(3, reminder.getDueDate());
            ps.setBigDecimal(4, reminder.getAmount());
            ps.setString(5, reminder.getReminderType());
            ps.setBoolean(6, reminder.getIsSent());
            ps.setBoolean(7, reminder.getIsActive());
            ps.setTimestamp(8, reminder.getSentDate());
            ps.setString(9, reminder.getNotes());
            ps.setTimestamp(10, new Timestamp(System.currentTimeMillis()));
            ps.setTimestamp(11, new Timestamp(System.currentTimeMillis()));
            return ps;
        }, keyHolder);
        
        if (rowsAffected > 0 && keyHolder.getKey() != null) {
            Long id = keyHolder.getKey().longValue();
            reminder.setId(id);
        }
        
        return reminder;
    }

    @Override
    public Optional<DebtReminder> findById(Long id) {
        String sql = "SELECT * FROM debt_reminders WHERE id = ?";
        List<DebtReminder> reminders = jdbcTemplate.query(sql, debtReminderRowMapper, id);
        return reminders.isEmpty() ? Optional.empty() : Optional.of(reminders.get(0));
    }

    @Override
    public List<DebtReminder> findByDebtId(Long debtId) {
        String sql = "SELECT * FROM debt_reminders WHERE debt_id = ? ORDER BY due_date DESC";
        return jdbcTemplate.query(sql, debtReminderRowMapper, debtId);
    }

    @Override
    public List<DebtReminder> findAll() {
        String sql = "SELECT * FROM debt_reminders ORDER BY due_date DESC";
        return jdbcTemplate.query(sql, debtReminderRowMapper);
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM debt_reminders WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void update(DebtReminder reminder) {
        String sql = "UPDATE debt_reminders SET debt_id = ?, reminder_date = ?, due_date = ?, " +
                    "amount = ?, reminder_type = ?, is_sent = ?, is_active = ?, sent_date = ?, " +
                    "notes = ?, updated_at = ? WHERE id = ?";
        
        jdbcTemplate.update(sql, 
            reminder.getDebtId(),
            reminder.getReminderDate(),
            reminder.getDueDate(),
            reminder.getAmount(),
            reminder.getReminderType(),
            reminder.getIsSent(),
            reminder.getIsActive(),
            reminder.getSentDate(),
            reminder.getNotes(),
            new Timestamp(System.currentTimeMillis()),
            reminder.getId());
    }

    public List<DebtReminder> findActiveByDebtId(Long debtId) {
        String sql = "SELECT * FROM debt_reminders WHERE debt_id = ? AND is_active = true ORDER BY due_date ASC";
        return jdbcTemplate.query(sql, debtReminderRowMapper, debtId);
    }

    public List<DebtReminder> findUpcomingReminders(Timestamp startDate, Timestamp endDate) {
        String sql = "SELECT * FROM debt_reminders WHERE reminder_date BETWEEN ? AND ? AND is_active = true ORDER BY reminder_date ASC";
        return jdbcTemplate.query(sql, debtReminderRowMapper, startDate, endDate);
    }

    public List<DebtReminder> findOverdueReminders(Timestamp currentDate) {
        String sql = "SELECT * FROM debt_reminders WHERE due_date < ? AND is_active = true ORDER BY due_date ASC";
        return jdbcTemplate.query(sql, debtReminderRowMapper, currentDate);
    }

    public List<DebtReminder> findUnsentReminders() {
        String sql = "SELECT * FROM debt_reminders WHERE is_sent = false AND is_active = true ORDER BY reminder_date ASC";
        return jdbcTemplate.query(sql, debtReminderRowMapper);
    }
} 