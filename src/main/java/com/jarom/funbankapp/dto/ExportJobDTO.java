package com.jarom.funbankapp.dto;

import java.time.LocalDateTime;

/**
 * DTO for export job responses
 */
public class ExportJobDTO {
    private Long id;
    private Long userId;
    private String jobType;
    private String format;
    private String status;
    private String filePath;
    private Long fileSize;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String errorMessage;
    private String downloadUrl;
    private Integer progress; // 0-100 percentage

    // Default constructor
    public ExportJobDTO() {}

    // Constructor with required fields
    public ExportJobDTO(Long id, Long userId, String jobType, String format, String status) {
        this.id = id;
        this.userId = userId;
        this.jobType = jobType;
        this.format = format;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.progress = 0;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    @Override
    public String toString() {
        return "ExportJobDTO{" +
                "id=" + id +
                ", userId=" + userId +
                ", jobType='" + jobType + '\'' +
                ", format='" + format + '\'' +
                ", status='" + status + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fileSize=" + fileSize +
                ", createdAt=" + createdAt +
                ", startedAt=" + startedAt +
                ", completedAt=" + completedAt +
                ", errorMessage='" + errorMessage + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", progress=" + progress +
                '}';
    }
} 