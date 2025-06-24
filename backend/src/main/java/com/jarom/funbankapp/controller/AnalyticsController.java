package com.jarom.funbankapp.controller;

import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.UserDAO;
import com.jarom.funbankapp.repository.TransactionDAO;
import com.jarom.funbankapp.repository.AccountDAO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/analytics")
@io.swagger.v3.oas.annotations.tags.Tag(name = "Analytics", description = "Financial analytics and reporting")
public class AnalyticsController {

    private final UserDAO userDAO;
    private final TransactionDAO transactionDAO;
    private final AccountDAO accountDAO;

    public AnalyticsController(UserDAO userDAO, TransactionDAO transactionDAO, AccountDAO accountDAO) {
        this.userDAO = userDAO;
        this.transactionDAO = transactionDAO;
        this.accountDAO = accountDAO;
    }

    @GetMapping("/spending")
    @Operation(summary = "Get spending analytics", description = "Retrieves spending analysis for the authenticated user.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Spending analytics retrieved successfully")
    })
    public ResponseEntity<?> getSpendingAnalytics(@RequestParam(defaultValue = "30") int days) {
        String username = getCurrentUsername();
        User user = userDAO.findByUsername(username);

        Map<String, BigDecimal> spendingByCategory = transactionDAO.getSpendingByCategory(user.getId(), days);
        
        BigDecimal totalSpent = spendingByCategory.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalSpent", totalSpent);
        analytics.put("spendingByCategory", spendingByCategory);
        analytics.put("period", days + " days");
        analytics.put("averageDailySpending", totalSpent.divide(new BigDecimal(days), 2, BigDecimal.ROUND_HALF_UP));

        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/income")
    @Operation(summary = "Get income analytics", description = "Retrieves income analysis for the authenticated user.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Income analytics retrieved successfully")
    })
    public ResponseEntity<?> getIncomeAnalytics(@RequestParam(defaultValue = "30") int days) {
        String username = getCurrentUsername();
        User user = userDAO.findByUsername(username);

        // This would require adding income tracking to TransactionDAO
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalIncome", BigDecimal.ZERO);
        analytics.put("incomeByCategory", new HashMap<>());
        analytics.put("period", days + " days");
        analytics.put("averageDailyIncome", BigDecimal.ZERO);

        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/net-worth")
    @Operation(summary = "Get net worth analytics", description = "Retrieves net worth analysis for the authenticated user.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Net worth analytics retrieved successfully")
    })
    public ResponseEntity<?> getNetWorthAnalytics() {
        String username = getCurrentUsername();
        User user = userDAO.findByUsername(username);

        List<com.jarom.funbankapp.model.Account> accounts = accountDAO.findByUserId(user.getId());
        
        BigDecimal totalAssets = accounts.stream()
                .map(com.jarom.funbankapp.model.Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // For now, assuming no debts (would need debt tracking)
        BigDecimal totalLiabilities = BigDecimal.ZERO;
        BigDecimal netWorth = totalAssets.subtract(totalLiabilities);

        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalAssets", totalAssets);
        analytics.put("totalLiabilities", totalLiabilities);
        analytics.put("netWorth", netWorth);
        analytics.put("accountCount", accounts.size());

        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/cash-flow")
    @Operation(summary = "Get cash flow analytics", description = "Retrieves cash flow analysis for the authenticated user.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cash flow analytics retrieved successfully")
    })
    public ResponseEntity<?> getCashFlowAnalytics(@RequestParam(defaultValue = "30") int days) {
        String username = getCurrentUsername();
        User user = userDAO.findByUsername(username);

        // This would require more sophisticated cash flow tracking
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("inflow", BigDecimal.ZERO);
        analytics.put("outflow", BigDecimal.ZERO);
        analytics.put("netCashFlow", BigDecimal.ZERO);
        analytics.put("period", days + " days");

        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/investment-performance")
    @Operation(summary = "Get investment performance analytics", description = "Retrieves investment performance analysis.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Investment performance analytics retrieved successfully")
    })
    public ResponseEntity<?> getInvestmentPerformance() {
        String username = getCurrentUsername();
        User user = userDAO.findByUsername(username);

        // This would require investment tracking
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalInvested", BigDecimal.ZERO);
        analytics.put("currentValue", BigDecimal.ZERO);
        analytics.put("totalReturn", BigDecimal.ZERO);
        analytics.put("returnPercentage", BigDecimal.ZERO);
        analytics.put("portfolioDiversification", new HashMap<>());

        return ResponseEntity.ok(analytics);
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
} 