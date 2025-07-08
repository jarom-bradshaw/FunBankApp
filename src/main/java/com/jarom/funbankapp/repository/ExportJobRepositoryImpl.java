package com.jarom.funbankapp.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.jarom.funbankapp.model.ExportJob;

/**
 * Implementation of ExportJobRepository using JDBC Template
 */
@Repository
public class ExportJobRepositoryImpl implements ExportJobRepository {

    private final JdbcTemplate jdbcTemplate;

    public ExportJobRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<ExportJob> exportJobRowMapper = (rs, rowNum) -> {
        ExportJob exportJob = new ExportJob();
        exportJob.setId(rs.getLong("id"));
        exportJob.setUserId(rs.getLong("user_id"));
        exportJob.setJobType(rs.getString("job_type"));
        exportJob.setFormat(rs.getString("format"));
        exportJob.setStatus(rs.getString("status"));
        exportJob.setFilePath(rs.getString("file_path"));
        exportJob.setFileSize(rs.getLong("file_size"));
        exportJob.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        
        Timestamp startedAt = rs.getTimestamp("started_at");
        if (startedAt != null) {
            exportJob.setStartedAt(startedAt.toLocalDateTime());
        }
        
        Timestamp completedAt = rs.getTimestamp("completed_at");
        if (completedAt != null) {
            exportJob.setCompletedAt(completedAt.toLocalDateTime());
        }
        
        exportJob.setErrorMessage(rs.getString("error_message"));
        exportJob.setParameters(rs.getString("parameters"));
        
        return exportJob;
    };

    @Override
    public ExportJob save(ExportJob exportJob) {
        String sql = "INSERT INTO export_jobs (user_id, job_type, format, status, created_at, parameters) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, exportJob.getUserId());
            ps.setString(2, exportJob.getJobType());
            ps.setString(3, exportJob.getFormat());
            ps.setString(4, exportJob.getStatus());
            ps.setTimestamp(5, Timestamp.valueOf(exportJob.getCreatedAt()));
            ps.setString(6, exportJob.getParameters());
            return ps;
        }, keyHolder);
        
        exportJob.setId(keyHolder.getKey().longValue());
        return exportJob;
    }

    @Override
    public Optional<ExportJob> findById(Long id) {
        String sql = "SELECT * FROM export_jobs WHERE id = ?";
        List<ExportJob> results = jdbcTemplate.query(sql, exportJobRowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<ExportJob> findByUserId(Long userId) {
        String sql = "SELECT * FROM export_jobs WHERE user_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, exportJobRowMapper, userId);
    }

    @Override
    public List<ExportJob> findByUserIdAndStatus(Long userId, String status) {
        String sql = "SELECT * FROM export_jobs WHERE user_id = ? AND status = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, exportJobRowMapper, userId, status);
    }

    @Override
    public boolean updateStatus(Long id, String status) {
        String sql = "UPDATE export_jobs SET status = ? WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, status, id);
        return rowsAffected > 0;
    }

    @Override
    public boolean updateFileInfo(Long id, String filePath, Long fileSize) {
        String sql = "UPDATE export_jobs SET file_path = ?, file_size = ?, completed_at = ? WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, filePath, fileSize, Timestamp.valueOf(LocalDateTime.now()), id);
        return rowsAffected > 0;
    }

    @Override
    public boolean updateProgress(Long id, Integer progress) {
        // Note: This would require adding a progress column to the table
        // For now, we'll use a placeholder implementation
        String sql = "UPDATE export_jobs SET status = ? WHERE id = ?";
        String status = progress == 100 ? "COMPLETED" : "PROCESSING";
        int rowsAffected = jdbcTemplate.update(sql, status, id);
        return rowsAffected > 0;
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM export_jobs WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        return rowsAffected > 0;
    }

    @Override
    public List<ExportJob> findOldJobs(int daysOld) {
        String sql = "SELECT * FROM export_jobs WHERE created_at < ? AND status IN ('COMPLETED', 'FAILED')";
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        return jdbcTemplate.query(sql, exportJobRowMapper, Timestamp.valueOf(cutoffDate));
    }

    @Override
    public long countByUserIdAndStatus(Long userId, String status) {
        String sql = "SELECT COUNT(*) FROM export_jobs WHERE user_id = ? AND status = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, userId, status);
    }
} 