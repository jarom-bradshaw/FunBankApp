package com.jarom.funbankapp.service;

import com.jarom.funbankapp.dto.ExportJobDTO;
import com.jarom.funbankapp.dto.ExportRequest;
import org.springframework.core.io.Resource;
import java.util.List;

/**
 * Service interface for export operations
 */
public interface ExportService {
    
    /**
     * Create a new export job
     */
    ExportJobDTO createExportJob(Long userId, ExportRequest request);
    
    /**
     * Get export job by ID
     */
    ExportJobDTO getExportJob(Long userId, Long jobId);
    
    /**
     * Get all export jobs for a user
     */
    List<ExportJobDTO> getUserExportJobs(Long userId);
    
    /**
     * Cancel an export job
     */
    boolean cancelExportJob(Long userId, Long jobId);
    
    /**
     * Download exported file
     */
    Resource downloadExportFile(Long userId, Long jobId);
    
    /**
     * Process export job asynchronously
     */
    void processExportJob(Long jobId);
    
    /**
     * Clean up old export files
     */
    void cleanupOldExports(int daysOld);
    
    /**
     * Get export job status
     */
    String getExportJobStatus(Long userId, Long jobId);
    
    /**
     * Get export job progress
     */
    Integer getExportJobProgress(Long userId, Long jobId);
} 