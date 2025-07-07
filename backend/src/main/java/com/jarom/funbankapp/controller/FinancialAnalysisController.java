package com.jarom.funbankapp.controller;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jarom.funbankapp.dto.ApiResponse;
import com.jarom.funbankapp.dto.FinancialReportDTO;
import com.jarom.funbankapp.service.FinancialAnalysisService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/financial-analysis")
@Tag(name = "Financial Analysis", description = "Advanced financial analysis and reporting")
@SecurityRequirement(name = "bearerAuth")
public class FinancialAnalysisController {

    private final FinancialAnalysisService financialAnalysisService;

    public FinancialAnalysisController(FinancialAnalysisService financialAnalysisService) {
        this.financialAnalysisService = financialAnalysisService;
    }

    @GetMapping("/report")
    @Operation(summary = "Generate financial report", description = "Generate comprehensive financial report for a date range")
    public ResponseEntity<ApiResponse<FinancialReportDTO>> generateFinancialReport(
            Authentication authentication,
            @Parameter(description = "Start date (YYYY-MM-DD)", example = "2024-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (YYYY-MM-DD)", example = "2024-12-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            String username = authentication.getName();
            FinancialReportDTO report = financialAnalysisService.generateFinancialReport(username, startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success("Financial report generated successfully", report));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to generate financial report: " + e.getMessage()));
        }
    }

    @GetMapping("/income-by-category")
    @Operation(summary = "Get income by category", description = "Retrieve income breakdown by category")
    public ResponseEntity<ApiResponse<Map<String, java.math.BigDecimal>>> getIncomeByCategory(
            Authentication authentication,
            @Parameter(description = "Number of days to look back", example = "30")
            @RequestParam(defaultValue = "30") int days) {
        try {
            String username = authentication.getName();
            Map<String, java.math.BigDecimal> income = financialAnalysisService.getIncomeByCategory(username, days);
            return ResponseEntity.ok(ApiResponse.success("Income by category retrieved successfully", income));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve income data: " + e.getMessage()));
        }
    }

    @GetMapping("/spending-by-month")
    @Operation(summary = "Get spending by month", description = "Retrieve spending breakdown by month")
    public ResponseEntity<ApiResponse<Map<String, java.math.BigDecimal>>> getSpendingByMonth(
            Authentication authentication,
            @Parameter(description = "Number of months to look back", example = "12")
            @RequestParam(defaultValue = "12") int months) {
        try {
            String username = authentication.getName();
            Map<String, java.math.BigDecimal> spending = financialAnalysisService.getSpendingByMonth(username, months);
            return ResponseEntity.ok(ApiResponse.success("Spending by month retrieved successfully", spending));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve spending data: " + e.getMessage()));
        }
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get transaction statistics", description = "Retrieve comprehensive transaction statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTransactionStatistics(
            Authentication authentication,
            @Parameter(description = "Number of days to look back", example = "30")
            @RequestParam(defaultValue = "30") int days) {
        try {
            String username = authentication.getName();
            Map<String, Object> statistics = financialAnalysisService.getTransactionStatistics(username, days);
            return ResponseEntity.ok(ApiResponse.success("Transaction statistics retrieved successfully", statistics));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve transaction statistics: " + e.getMessage()));
        }
    }

    @GetMapping("/trends")
    @Operation(summary = "Get financial trends", description = "Retrieve financial trend analysis")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFinancialTrends(
            Authentication authentication,
            @Parameter(description = "Number of months to analyze", example = "12")
            @RequestParam(defaultValue = "12") int months) {
        try {
            String username = authentication.getName();
            Map<String, Object> trends = financialAnalysisService.getFinancialTrends(username, months);
            return ResponseEntity.ok(ApiResponse.success("Financial trends retrieved successfully", trends));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve financial trends: " + e.getMessage()));
        }
    }
} 