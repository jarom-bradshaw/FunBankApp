package com.jarom.funbankapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jarom.funbankapp.dto.ExportJobDTO;
import com.jarom.funbankapp.dto.ExportRequest;
import com.jarom.funbankapp.model.ExportJob;
import com.jarom.funbankapp.model.ExportStatus;
import com.jarom.funbankapp.repository.ExportJobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of ExportService
 */
@Service
@Transactional
public class ExportServiceImpl implements ExportService {

    private static final Logger logger = LoggerFactory.getLogger(ExportServiceImpl.class);

    @Autowired
    private ExportJobRepository exportJobRepository;

    @Autowired
    private CSVExportService csvExportService;

    @Autowired
    private PDFExportService pdfExportService;

    @Autowired
    private ExcelExportService excelExportService;

    @Autowired
    private JSONExportService jsonExportService;

    @Value("${app.export.storage.path:./exports}")
    private String exportStoragePath;

    @Value("${app.export.retention.days:7}")
    private int exportRetentionDays;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ExportJobDTO createExportJob(Long userId, ExportRequest request) {
        logger.info("Creating export job for user {}: type={}, format={}", userId, request.getExportType(), request.getFormat());

        // Validate request
        validateExportRequest(request);

        // Create export job
        ExportJob exportJob = new ExportJob(userId, request.getExportType(), request.getFormat());
        exportJob.setCreatedAt(LocalDateTime.now());
        exportJob.setStatus(ExportStatus.PENDING.getValue());

        // Store parameters as JSON
        try {
            exportJob.setParameters(objectMapper.writeValueAsString(request));
        } catch (IOException e) {
            logger.error("Failed to serialize export parameters", e);
            throw new RuntimeException("Failed to create export job", e);
        }

        // Save to database
        ExportJob savedJob = exportJobRepository.save(exportJob);

        // Convert to DTO
        ExportJobDTO jobDTO = convertToDTO(savedJob);

        // Start async processing
        processExportJob(savedJob.getId());

        logger.info("Export job created successfully: {}", savedJob.getId());
        return jobDTO;
    }

    @Override
    public ExportJobDTO getExportJob(Long userId, Long jobId) {
        logger.debug("Getting export job {} for user {}", jobId, userId);

        Optional<ExportJob> exportJob = exportJobRepository.findById(jobId);
        if (exportJob.isEmpty() || !exportJob.get().getUserId().equals(userId)) {
            throw new RuntimeException("Export job not found or access denied");
        }

        return convertToDTO(exportJob.get());
    }

    @Override
    public List<ExportJobDTO> getUserExportJobs(Long userId) {
        logger.debug("Getting all export jobs for user {}", userId);

        List<ExportJob> exportJobs = exportJobRepository.findByUserId(userId);
        return exportJobs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean cancelExportJob(Long userId, Long jobId) {
        logger.info("Cancelling export job {} for user {}", jobId, userId);

        Optional<ExportJob> exportJob = exportJobRepository.findById(jobId);
        if (exportJob.isEmpty() || !exportJob.get().getUserId().equals(userId)) {
            return false;
        }

        ExportJob job = exportJob.get();
        if (job.getStatus().equals(ExportStatus.COMPLETED.getValue()) || 
            job.getStatus().equals(ExportStatus.FAILED.getValue())) {
            return false; // Cannot cancel completed or failed jobs
        }

        return exportJobRepository.updateStatus(jobId, ExportStatus.CANCELLED.getValue());
    }

    @Override
    public Resource downloadExportFile(Long userId, Long jobId) {
        logger.info("Downloading export file for job {} by user {}", jobId, userId);

        Optional<ExportJob> exportJob = exportJobRepository.findById(jobId);
        if (exportJob.isEmpty() || !exportJob.get().getUserId().equals(userId)) {
            throw new RuntimeException("Export job not found or access denied");
        }

        ExportJob job = exportJob.get();
        if (!job.getStatus().equals(ExportStatus.COMPLETED.getValue())) {
            throw new RuntimeException("Export job is not completed");
        }

        if (job.getFilePath() == null || job.getFilePath().isEmpty()) {
            throw new RuntimeException("Export file not found");
        }

        File file = new File(job.getFilePath());
        if (!file.exists()) {
            throw new RuntimeException("Export file not found on disk");
        }

        return new FileSystemResource(file);
    }

    @Override
    @Async
    public void processExportJob(Long jobId) {
        logger.info("Processing export job: {}", jobId);

        Optional<ExportJob> exportJobOpt = exportJobRepository.findById(jobId);
        if (exportJobOpt.isEmpty()) {
            logger.error("Export job not found: {}", jobId);
            return;
        }

        ExportJob exportJob = exportJobOpt.get();

        try {
            // Update status to processing
            exportJobRepository.updateStatus(jobId, ExportStatus.PROCESSING.getValue());
            exportJobRepository.updateProgress(jobId, 10);

            // Parse export request
            ExportRequest request = objectMapper.readValue(exportJob.getParameters(), ExportRequest.class);

            // Create export directory if it doesn't exist
            Path exportDir = Paths.get(exportStoragePath);
            if (!Files.exists(exportDir)) {
                Files.createDirectories(exportDir);
            }

            // Generate filename
            String fileName = generateFileName(request, exportJob.getId());
            String filePath = exportDir.resolve(fileName).toString();

            // Process export based on format
            File exportFile = null;
            switch (request.getFormat().toUpperCase()) {
                case "CSV":
                    exportFile = csvExportService.exportData(exportJob.getUserId(), request, filePath);
                    break;
                case "PDF":
                    exportFile = pdfExportService.exportData(exportJob.getUserId(), request, filePath);
                    break;
                case "EXCEL":
                    exportFile = excelExportService.exportData(exportJob.getUserId(), request, filePath);
                    break;
                case "JSON":
                    exportFile = jsonExportService.exportData(exportJob.getUserId(), request, filePath);
                    break;
                default:
                    throw new RuntimeException("Unsupported export format: " + request.getFormat());
            }

            // Update job with file information
            exportJobRepository.updateFileInfo(jobId, filePath, exportFile.length());
            exportJobRepository.updateStatus(jobId, ExportStatus.COMPLETED.getValue());
            exportJobRepository.updateProgress(jobId, 100);

            logger.info("Export job completed successfully: {}", jobId);

        } catch (Exception e) {
            logger.error("Failed to process export job: {}", jobId, e);
            exportJobRepository.updateStatus(jobId, ExportStatus.FAILED.getValue());
            // Note: Would need to add error_message column to update this
        }
    }

    @Override
    public void cleanupOldExports(int daysOld) {
        logger.info("Cleaning up export files older than {} days", daysOld);

        List<ExportJob> oldJobs = exportJobRepository.findOldJobs(daysOld);
        for (ExportJob job : oldJobs) {
            if (job.getFilePath() != null && !job.getFilePath().isEmpty()) {
                try {
                    File file = new File(job.getFilePath());
                    if (file.exists() && file.delete()) {
                        logger.debug("Deleted old export file: {}", job.getFilePath());
                    }
                } catch (Exception e) {
                    logger.warn("Failed to delete old export file: {}", job.getFilePath(), e);
                }
            }
        }
    }

    @Override
    public String getExportJobStatus(Long userId, Long jobId) {
        Optional<ExportJob> exportJob = exportJobRepository.findById(jobId);
        if (exportJob.isEmpty() || !exportJob.get().getUserId().equals(userId)) {
            throw new RuntimeException("Export job not found or access denied");
        }
        return exportJob.get().getStatus();
    }

    @Override
    public Integer getExportJobProgress(Long userId, Long jobId) {
        Optional<ExportJob> exportJob = exportJobRepository.findById(jobId);
        if (exportJob.isEmpty() || !exportJob.get().getUserId().equals(userId)) {
            throw new RuntimeException("Export job not found or access denied");
        }

        String status = exportJob.get().getStatus();
        switch (status) {
            case "PENDING":
                return 0;
            case "PROCESSING":
                return 50; // Placeholder - would need progress column
            case "COMPLETED":
                return 100;
            case "FAILED":
            case "CANCELLED":
                return 0;
            default:
                return 0;
        }
    }

    private void validateExportRequest(ExportRequest request) {
        if (request.getExportType() == null || request.getExportType().trim().isEmpty()) {
            throw new RuntimeException("Export type is required");
        }

        if (request.getFormat() == null || request.getFormat().trim().isEmpty()) {
            throw new RuntimeException("Export format is required");
        }

        // Validate date range if provided
        if (request.getStartDate() != null && request.getEndDate() != null) {
            if (request.getStartDate().isAfter(request.getEndDate())) {
                throw new RuntimeException("Start date cannot be after end date");
            }
        }
    }

    private String generateFileName(ExportRequest request, Long jobId) {
        String timestamp = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String extension = request.getFormat().toLowerCase();
        
        if (request.getFileName() != null && !request.getFileName().trim().isEmpty()) {
            return request.getFileName() + "_" + timestamp + "." + extension;
        }
        
        return request.getExportType() + "_" + timestamp + "_" + jobId + "." + extension;
    }

    private ExportJobDTO convertToDTO(ExportJob exportJob) {
        ExportJobDTO dto = new ExportJobDTO(
            exportJob.getId(),
            exportJob.getUserId(),
            exportJob.getJobType(),
            exportJob.getFormat(),
            exportJob.getStatus()
        );

        dto.setFilePath(exportJob.getFilePath());
        dto.setFileSize(exportJob.getFileSize());
        dto.setCreatedAt(exportJob.getCreatedAt());
        dto.setStartedAt(exportJob.getStartedAt());
        dto.setCompletedAt(exportJob.getCompletedAt());
        dto.setErrorMessage(exportJob.getErrorMessage());
        dto.setProgress(getExportJobProgress(exportJob.getUserId(), exportJob.getId()));

        // Generate download URL if file exists
        if (exportJob.getFilePath() != null && !exportJob.getFilePath().isEmpty()) {
            dto.setDownloadUrl("/api/export/jobs/" + exportJob.getId() + "/download");
        }

        return dto;
    }
} 