package com.jarom.funbankapp.model;

/**
 * Enum representing supported export formats
 */
public enum ExportFormat {
    PDF("pdf", "application/pdf", "Portable Document Format"),
    CSV("csv", "text/csv", "Comma Separated Values"),
    EXCEL("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "Microsoft Excel"),
    JSON("json", "application/json", "JavaScript Object Notation");

    private final String extension;
    private final String mimeType;
    private final String description;

    ExportFormat(String extension, String mimeType, String description) {
        this.extension = extension;
        this.mimeType = mimeType;
        this.description = description;
    }

    public String getExtension() {
        return extension;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getDescription() {
        return description;
    }

    public static ExportFormat fromString(String format) {
        for (ExportFormat exportFormat : ExportFormat.values()) {
            if (exportFormat.name().equalsIgnoreCase(format) || 
                exportFormat.extension.equalsIgnoreCase(format)) {
                return exportFormat;
            }
        }
        throw new IllegalArgumentException("Unsupported export format: " + format);
    }
} 