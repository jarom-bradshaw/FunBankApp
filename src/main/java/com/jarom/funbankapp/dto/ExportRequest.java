package com.jarom.funbankapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.Map;

/**
 * DTO for export requests
 */
public class ExportRequest {
    
    @NotBlank(message = "Export type is required")
    @Pattern(regexp = "^(transactions|reports|debts|investments|budgets|goals|all)$", 
             message = "Export type must be one of: transactions, reports, debts, investments, budgets, goals, all")
    private String exportType;
    
    @NotBlank(message = "Export format is required")
    @Pattern(regexp = "^(PDF|CSV|EXCEL|JSON)$", 
             message = "Export format must be one of: PDF, CSV, EXCEL, JSON")
    private String format;
    
    private LocalDate startDate;
    private LocalDate endDate;
    private Map<String, Object> filters; // Additional filters like categories, accounts, etc.
    private String fileName; // Optional custom filename

    // Default constructor
    public ExportRequest() {}

    // Constructor with required fields
    public ExportRequest(String exportType, String format) {
        this.exportType = exportType;
        this.format = format;
    }

    // Getters and Setters
    public String getExportType() {
        return exportType;
    }

    public void setExportType(String exportType) {
        this.exportType = exportType;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Map<String, Object> getFilters() {
        return filters;
    }

    public void setFilters(Map<String, Object> filters) {
        this.filters = filters;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "ExportRequest{" +
                "exportType='" + exportType + '\'' +
                ", format='" + format + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", filters=" + filters +
                ", fileName='" + fileName + '\'' +
                '}';
    }
} 