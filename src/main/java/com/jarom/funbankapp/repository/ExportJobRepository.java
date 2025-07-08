package com.jarom.funbankapp.repository;

import com.jarom.funbankapp.model.ExportJob;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for ExportJob operations
 */
public interface ExportJobRepository {
    
    /**
     * Save a new export job
     */
    ExportJob save(ExportJob exportJob);
    
    /**
     * Find export job by ID
     */
    Optional<ExportJob> findById(Long id);
    
    /**
     * Find all export jobs for a user
     */
    List<ExportJob> findByUserId(Long userId);
    
    /**
     * Find export jobs by status for a user
     */
    List<ExportJob> findByUserIdAndStatus(Long userId, String status);
    
    /**
     * Update export job status
     */
    boolean updateStatus(Long id, String status);
    
    /**
     * Update export job with file information
     */
    boolean updateFileInfo(Long id, String filePath, Long fileSize);
    
    /**
     * Update export job progress
     */
    boolean updateProgress(Long id, Integer progress);
    
    /**
     * Delete export job
     */
    boolean deleteById(Long id);
    
    /**
     * Find export jobs that are older than specified days
     */
    List<ExportJob> findOldJobs(int daysOld);
    
    /**
     * Count export jobs by status for a user
     */
    long countByUserIdAndStatus(Long userId, String status);
} 