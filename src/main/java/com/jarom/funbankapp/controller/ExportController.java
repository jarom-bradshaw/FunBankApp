package com.jarom.funbankapp.controller;

import com.jarom.funbankapp.dto.ApiResponse;
import com.jarom.funbankapp.dto.ExportJobDTO;
import com.jarom.funbankapp.dto.ExportRequest;
import com.jarom.funbankapp.service.ExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for export operations
 */
@RestController
@RequestMapping("/api/export")
@Tag(name = "Export", description = "Export financial data in various formats")
public class ExportController {

    @Autowired
    private ExportService exportService;

    @PostMapping("/transactions")
    @Operation(summary = "Export transactions", description = "Export transactions by date range and filters")
    public ResponseEntity<ApiResponse<ExportJobDTO>> exportTransactions(
            @Parameter(description = "Export request with format and filters") @Valid @RequestBody ExportRequest request,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        ExportJobDTO job = exportService.createExportJob(userId, request);
        
        return ResponseEntity.ok(ApiResponse.success("Export job created successfully", job));
    }

    @PostMapping("/reports")
    @Operation(summary = "Export financial reports", description = "Export comprehensive financial reports")
    public ResponseEntity<ApiResponse<ExportJobDTO>> exportReports(
            @Parameter(description = "Export request for financial reports") @Valid @RequestBody ExportRequest request,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        request.setExportType("reports");
        ExportJobDTO job = exportService.createExportJob(userId, request);
        
        return ResponseEntity.ok(ApiResponse.success("Financial report export job created successfully", job));
    }

    @PostMapping("/debts")
    @Operation(summary = "Export debt summary", description = "Export debt information and payment history")
    public ResponseEntity<ApiResponse<ExportJobDTO>> exportDebts(
            @Parameter(description = "Export request for debt data") @Valid @RequestBody ExportRequest request,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        request.setExportType("debts");
        ExportJobDTO job = exportService.createExportJob(userId, request);
        
        return ResponseEntity.ok(ApiResponse.success("Debt export job created successfully", job));
    }

    @PostMapping("/investments")
    @Operation(summary = "Export investment portfolio", description = "Export investment holdings and performance")
    public ResponseEntity<ApiResponse<ExportJobDTO>> exportInvestments(
            @Parameter(description = "Export request for investment data") @Valid @RequestBody ExportRequest request,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        request.setExportType("investments");
        ExportJobDTO job = exportService.createExportJob(userId, request);
        
        return ResponseEntity.ok(ApiResponse.success("Investment export job created successfully", job));
    }

    @PostMapping("/budgets")
    @Operation(summary = "Export budget data", description = "Export budget information and spending analysis")
    public ResponseEntity<ApiResponse<ExportJobDTO>> exportBudgets(
            @Parameter(description = "Export request for budget data") @Valid @RequestBody ExportRequest request,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        request.setExportType("budgets");
        ExportJobDTO job = exportService.createExportJob(userId, request);
        
        return ResponseEntity.ok(ApiResponse.success("Budget export job created successfully", job));
    }

    @PostMapping("/goals")
    @Operation(summary = "Export financial goals", description = "Export financial goals and progress tracking")
    public ResponseEntity<ApiResponse<ExportJobDTO>> exportGoals(
            @Parameter(description = "Export request for goal data") @Valid @RequestBody ExportRequest request,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        request.setExportType("goals");
        ExportJobDTO job = exportService.createExportJob(userId, request);
        
        return ResponseEntity.ok(ApiResponse.success("Goal export job created successfully", job));
    }

    @PostMapping("/all")
    @Operation(summary = "Export all financial data", description = "Export comprehensive financial data in one file")
    public ResponseEntity<ApiResponse<ExportJobDTO>> exportAllData(
            @Parameter(description = "Export request for all financial data") @Valid @RequestBody ExportRequest request,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        request.setExportType("all");
        ExportJobDTO job = exportService.createExportJob(userId, request);
        
        return ResponseEntity.ok(ApiResponse.success("Complete financial data export job created successfully", job));
    }

    @GetMapping("/jobs")
    @Operation(summary = "List export jobs", description = "Get all export jobs for the current user")
    public ResponseEntity<ApiResponse<List<ExportJobDTO>>> getExportJobs(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<ExportJobDTO> jobs = exportService.getUserExportJobs(userId);
        
        return ResponseEntity.ok(ApiResponse.success("Export jobs retrieved successfully", jobs));
    }

    @GetMapping("/jobs/{jobId}")
    @Operation(summary = "Get export job status", description = "Get the status and details of a specific export job")
    public ResponseEntity<ApiResponse<ExportJobDTO>> getExportJob(
            @Parameter(description = "Export job ID") @PathVariable Long jobId,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        ExportJobDTO job = exportService.getExportJob(userId, jobId);
        
        return ResponseEntity.ok(ApiResponse.success("Export job details retrieved successfully", job));
    }

    @DeleteMapping("/jobs/{jobId}")
    @Operation(summary = "Cancel export job", description = "Cancel a pending or processing export job")
    public ResponseEntity<ApiResponse<String>> cancelExportJob(
            @Parameter(description = "Export job ID") @PathVariable Long jobId,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        boolean cancelled = exportService.cancelExportJob(userId, jobId);
        
        if (cancelled) {
            return ResponseEntity.ok(ApiResponse.success("Export job cancelled successfully", "Job cancelled"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to cancel export job"));
        }
    }

    @GetMapping("/jobs/{jobId}/download")
    @Operation(summary = "Download exported file", description = "Download the exported file when job is completed")
    public ResponseEntity<Resource> downloadExportFile(
            @Parameter(description = "Export job ID") @PathVariable Long jobId,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        Resource file = exportService.downloadExportFile(userId, jobId);
        
        // Determine content type based on file extension
        String contentType = "application/octet-stream";
        String filename = file.getFilename();
        if (filename != null) {
            if (filename.endsWith(".pdf")) {
                contentType = "application/pdf";
            } else if (filename.endsWith(".csv")) {
                contentType = "text/csv";
            } else if (filename.endsWith(".xlsx")) {
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            } else if (filename.endsWith(".json")) {
                contentType = "application/json";
            }
        }
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(file);
    }

    @GetMapping("/jobs/{jobId}/status")
    @Operation(summary = "Get job status", description = "Get the current status of an export job")
    public ResponseEntity<ApiResponse<String>> getJobStatus(
            @Parameter(description = "Export job ID") @PathVariable Long jobId,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        String status = exportService.getExportJobStatus(userId, jobId);
        
        return ResponseEntity.ok(ApiResponse.success("Job status retrieved successfully", status));
    }

    @GetMapping("/jobs/{jobId}/progress")
    @Operation(summary = "Get job progress", description = "Get the progress percentage of an export job")
    public ResponseEntity<ApiResponse<Integer>> getJobProgress(
            @Parameter(description = "Export job ID") @PathVariable Long jobId,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        Integer progress = exportService.getExportJobProgress(userId, jobId);
        
        return ResponseEntity.ok(ApiResponse.success("Job progress retrieved successfully", progress));
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        // This is a placeholder - in a real implementation, you would extract the user ID from the JWT token
        // For now, we'll return a default user ID
        return 1L;
    }
} 