package com.jarom.funbankapp.model;

/**
 * Enum representing export job statuses
 */
public enum ExportStatus {
    PENDING("PENDING", "Job is queued and waiting to be processed"),
    PROCESSING("PROCESSING", "Job is currently being processed"),
    COMPLETED("COMPLETED", "Job has been completed successfully"),
    FAILED("FAILED", "Job failed to complete"),
    CANCELLED("CANCELLED", "Job was cancelled by user");

    private final String value;
    private final String description;

    ExportStatus(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public static ExportStatus fromString(String status) {
        for (ExportStatus exportStatus : ExportStatus.values()) {
            if (exportStatus.value.equalsIgnoreCase(status)) {
                return exportStatus;
            }
        }
        throw new IllegalArgumentException("Unsupported export status: " + status);
    }

    @Override
    public String toString() {
        return value;
    }
} 