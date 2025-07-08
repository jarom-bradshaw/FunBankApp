package com.jarom.funbankapp.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jarom.funbankapp.dto.DebtDTO;
import com.jarom.funbankapp.dto.DebtPaymentDTO;
import com.jarom.funbankapp.exception.ResourceNotFoundException;
import com.jarom.funbankapp.exception.UnauthorizedException;
import com.jarom.funbankapp.model.Debt;
import com.jarom.funbankapp.model.DebtPayment;
import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.DebtPaymentRepository;
import com.jarom.funbankapp.repository.DebtRepository;
import com.jarom.funbankapp.repository.UserRepository;

@Service
public class DebtServiceImpl implements DebtService {

    private final DebtRepository debtRepository;
    private final DebtPaymentRepository debtPaymentRepository;
    private final UserRepository userRepository;

    public DebtServiceImpl(DebtRepository debtRepository, DebtPaymentRepository debtPaymentRepository, UserRepository userRepository) {
        this.debtRepository = debtRepository;
        this.debtPaymentRepository = debtPaymentRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public DebtDTO createDebt(DebtDTO debtDTO) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        // Validate debt data
        validateDebtData(debtDTO);
        
        // Create debt model
        Debt debt = new Debt();
        debt.setUserId(user.getId());
        debt.setName(debtDTO.getName());
        debt.setDebtType(debtDTO.getDebtType());
        debt.setOriginalAmount(debtDTO.getOriginalAmount());
        debt.setCurrentBalance(debtDTO.getOriginalAmount());
        debt.setInterestRate(debtDTO.getInterestRate());
        debt.setMinimumPayment(debtDTO.getMinimumPayment());
        debt.setDueDate(debtDTO.getDueDate());
        debt.setStatus(debtDTO.getStatus() != null ? debtDTO.getStatus() : "active");
        debt.setPriority(debtDTO.getPriority() != null ? debtDTO.getPriority() : "medium");
        debt.setNotes(debtDTO.getNotes());
        debt.setStartDate(new Timestamp(System.currentTimeMillis()));
        debt.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        debt.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        
        // Save to database
        Debt savedDebt = debtRepository.save(debt);
        
        return convertToDTO(savedDebt);
    }

    @Override
    public List<DebtDTO> getUserDebts() {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        List<Debt> debts = debtRepository.findByUserId(user.getId());
        return debts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DebtDTO getDebtById(Long debtId) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        Debt debt = debtRepository.findById(debtId)
                .orElseThrow(() -> new ResourceNotFoundException("Debt", "id", debtId));
        
        if (!debt.getUserId().equals(user.getId())) {
            throw new UnauthorizedException("You don't own this debt");
        }
        
        return convertToDTO(debt);
    }

    @Override
    @Transactional
    public DebtDTO updateDebt(Long debtId, DebtDTO debtDTO) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        Debt debt = debtRepository.findById(debtId)
                .orElseThrow(() -> new ResourceNotFoundException("Debt", "id", debtId));
        
        if (!debt.getUserId().equals(user.getId())) {
            throw new UnauthorizedException("You don't own this debt");
        }
        
        // Validate debt data
        validateDebtData(debtDTO);
        
        // Update fields
        if (debtDTO.getName() != null) {
            debt.setName(debtDTO.getName());
        }
        if (debtDTO.getDebtType() != null) {
            debt.setDebtType(debtDTO.getDebtType());
        }
        if (debtDTO.getOriginalAmount() != null) {
            debt.setOriginalAmount(debtDTO.getOriginalAmount());
        }
        if (debtDTO.getInterestRate() != null) {
            debt.setInterestRate(debtDTO.getInterestRate());
        }
        if (debtDTO.getMinimumPayment() != null) {
            debt.setMinimumPayment(debtDTO.getMinimumPayment());
        }
        if (debtDTO.getDueDate() != null) {
            debt.setDueDate(debtDTO.getDueDate());
        }
        if (debtDTO.getStatus() != null) {
            debt.setStatus(debtDTO.getStatus());
        }
        if (debtDTO.getPriority() != null) {
            debt.setPriority(debtDTO.getPriority());
        }
        if (debtDTO.getNotes() != null) {
            debt.setNotes(debtDTO.getNotes());
        }
        
        debt.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        
        // Save to database
        Debt updatedDebt = debtRepository.save(debt);
        
        return convertToDTO(updatedDebt);
    }

    @Override
    @Transactional
    public void deleteDebt(Long debtId) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        Debt debt = debtRepository.findById(debtId)
                .orElseThrow(() -> new ResourceNotFoundException("Debt", "id", debtId));
        
        if (!debt.getUserId().equals(user.getId())) {
            throw new UnauthorizedException("You don't own this debt");
        }
        
        // Check if debt has remaining balance
        if (debt.getCurrentBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new RuntimeException("Cannot delete debt with remaining balance");
        }
        
        debtRepository.deleteById(debtId);
    }

    @Override
    @Transactional
    public DebtDTO makePayment(Long debtId, DebtPaymentDTO paymentDTO) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        Debt debt = debtRepository.findById(debtId)
                .orElseThrow(() -> new ResourceNotFoundException("Debt", "id", debtId));
        
        if (!debt.getUserId().equals(user.getId())) {
            throw new UnauthorizedException("You don't own this debt");
        }
        
        // Validate payment
        if (paymentDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than 0");
        }
        
        if (paymentDTO.getAmount().compareTo(debt.getCurrentBalance()) > 0) {
            throw new IllegalArgumentException("Payment amount cannot exceed current balance");
        }
        
        // Create payment record
        DebtPayment payment = new DebtPayment();
        payment.setDebtId(debtId);
        payment.setAmount(paymentDTO.getAmount());
        payment.setPaymentType(paymentDTO.getPaymentType() != null ? paymentDTO.getPaymentType() : "extra");
        payment.setNotes(paymentDTO.getNotes());
        payment.setPaymentDate(new Timestamp(System.currentTimeMillis()));
        payment.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        payment.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        
        debtPaymentRepository.save(payment);
        
        // Update debt balance
        BigDecimal newBalance = debt.getCurrentBalance().subtract(paymentDTO.getAmount());
        debt.setCurrentBalance(newBalance);
        
        // Update debt status if paid off
        if (newBalance.compareTo(BigDecimal.ZERO) <= 0) {
            debt.setStatus("paid_off");
            debt.setEndDate(new Timestamp(System.currentTimeMillis()));
        }
        
        debt.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        
        // Save updated debt
        Debt updatedDebt = debtRepository.save(debt);
        
        return convertToDTO(updatedDebt);
    }

    @Override
    public List<DebtPaymentDTO> getPaymentHistory(Long debtId) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        Debt debt = debtRepository.findById(debtId)
                .orElseThrow(() -> new ResourceNotFoundException("Debt", "id", debtId));
        
        if (!debt.getUserId().equals(user.getId())) {
            throw new UnauthorizedException("You don't own this debt");
        }
        
        List<DebtPayment> payments = debtPaymentRepository.findByDebtId(debtId);
        return payments.stream()
                .map(this::convertPaymentToDTO)
                .sorted(Comparator.comparing(DebtPaymentDTO::getPaymentDate).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getDebtSummary() {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        List<Debt> debts = debtRepository.findByUserId(user.getId());
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalDebts", debts.size());
        summary.put("totalDebtAmount", getTotalDebtAmount());
        summary.put("totalMinimumPayments", getTotalMinimumPayments());
        
        // Count by status
        Map<String, Long> statusCount = debts.stream()
                .collect(Collectors.groupingBy(Debt::getStatus, Collectors.counting()));
        summary.put("statusBreakdown", statusCount);
        
        // Count by type
        Map<String, Long> typeCount = debts.stream()
                .collect(Collectors.groupingBy(Debt::getDebtType, Collectors.counting()));
        summary.put("typeBreakdown", typeCount);
        
        // Count by priority
        Map<String, Long> priorityCount = debts.stream()
                .collect(Collectors.groupingBy(Debt::getPriority, Collectors.counting()));
        summary.put("priorityBreakdown", priorityCount);
        
        return summary;
    }

    @Override
    public Map<String, Object> getDebtAnalysis() {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        List<Debt> debts = debtRepository.findByUserId(user.getId());
        
        Map<String, Object> analysis = new HashMap<>();
        
        // Average interest rate
        double avgInterestRate = debts.stream()
                .mapToDouble(d -> d.getInterestRate().doubleValue())
                .average()
                .orElse(0.0);
        analysis.put("averageInterestRate", BigDecimal.valueOf(avgInterestRate).setScale(2, RoundingMode.HALF_UP));
        
        // Highest interest debt
        debts.stream()
                .max(Comparator.comparing(Debt::getInterestRate))
                .ifPresent(debt -> analysis.put("highestInterestDebt", convertToDTO(debt)));
        
        // Largest debt
        debts.stream()
                .max(Comparator.comparing(Debt::getCurrentBalance))
                .ifPresent(debt -> analysis.put("largestDebt", convertToDTO(debt)));
        
        // Debt to income ratio (placeholder - would need income data)
        analysis.put("debtToIncomeRatio", "N/A - Income data not available");
        
        return analysis;
    }

    @Override
    public Map<String, Object> calculatePayoffTimeline(BigDecimal monthlyPaymentAmount, String strategy) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        List<Debt> debts = debtRepository.findByUserId(user.getId());
        
        if (debts.isEmpty()) {
            return Map.of("message", "No debts found");
        }
        
        // Sort debts based on strategy
        List<Debt> sortedDebts = new ArrayList<>(debts);
        if ("snowball".equals(strategy)) {
            // Sort by balance (lowest first)
            sortedDebts.sort(Comparator.comparing(Debt::getCurrentBalance));
        } else if ("avalanche".equals(strategy)) {
            // Sort by interest rate (highest first)
            sortedDebts.sort(Comparator.comparing(Debt::getInterestRate).reversed());
        }
        
        Map<String, Object> timeline = new HashMap<>();
        timeline.put("strategy", strategy);
        timeline.put("monthlyPaymentAmount", monthlyPaymentAmount);
        timeline.put("totalDebtAmount", getTotalDebtAmount());
        
        // Calculate payoff timeline (simplified calculation)
        BigDecimal remainingPayment = monthlyPaymentAmount;
        int totalMonths = 0;
        List<Map<String, Object>> payoffOrder = new ArrayList<>();
        
        for (Debt debt : sortedDebts) {
            if (debt.getCurrentBalance().compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            
            BigDecimal debtBalance = debt.getCurrentBalance();
            int monthsToPayoff = 0;
            
            while (debtBalance.compareTo(BigDecimal.ZERO) > 0 && remainingPayment.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal payment = remainingPayment.min(debtBalance);
                debtBalance = debtBalance.subtract(payment);
                remainingPayment = remainingPayment.subtract(payment);
                monthsToPayoff++;
            }
            
            Map<String, Object> debtPayoff = new HashMap<>();
            debtPayoff.put("debtId", debt.getId());
            debtPayoff.put("debtName", debt.getName());
            debtPayoff.put("monthsToPayoff", monthsToPayoff);
            debtPayoff.put("remainingBalance", debtBalance);
            payoffOrder.add(debtPayoff);
            
            totalMonths += monthsToPayoff;
        }
        
        timeline.put("totalMonthsToPayoff", totalMonths);
        timeline.put("payoffOrder", payoffOrder);
        
        return timeline;
    }

    @Override
    public List<DebtDTO> getDebtsByType(String debtType) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        List<Debt> debts = debtRepository.findByUserId(user.getId());
        return debts.stream()
                .filter(debt -> debtType.equals(debt.getDebtType()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DebtDTO> getDebtsByPriority(String priority) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        List<Debt> debts = debtRepository.findByUserId(user.getId());
        return debts.stream()
                .filter(debt -> priority.equals(debt.getPriority()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal getTotalDebtAmount() {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        List<Debt> debts = debtRepository.findByUserId(user.getId());
        return debts.stream()
                .map(Debt::getCurrentBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal getTotalMinimumPayments() {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        List<Debt> debts = debtRepository.findByUserId(user.getId());
        return debts.stream()
                .map(Debt::getMinimumPayment)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public boolean userOwnsDebt(Long debtId) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        return debtRepository.findById(debtId)
                .map(debt -> debt.getUserId().equals(user.getId()))
                .orElse(false);
    }

    // Helper methods
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
    
    private void validateDebtData(DebtDTO debtDTO) {
        if (debtDTO.getName() == null || debtDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Debt name is required");
        }
        if (debtDTO.getDebtType() == null || debtDTO.getDebtType().trim().isEmpty()) {
            throw new IllegalArgumentException("Debt type is required");
        }
        if (debtDTO.getOriginalAmount() == null || debtDTO.getOriginalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Original amount must be greater than 0");
        }
        if (debtDTO.getInterestRate() == null || debtDTO.getInterestRate().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Interest rate cannot be negative");
        }
    }
    
    private DebtDTO convertToDTO(Debt debt) {
        DebtDTO dto = new DebtDTO();
        dto.setId(debt.getId());
        dto.setName(debt.getName());
        dto.setDebtType(debt.getDebtType());
        dto.setOriginalAmount(debt.getOriginalAmount());
        dto.setCurrentBalance(debt.getCurrentBalance());
        dto.setInterestRate(debt.getInterestRate());
        dto.setMinimumPayment(debt.getMinimumPayment());
        dto.setDueDate(debt.getDueDate());
        dto.setStatus(debt.getStatus());
        dto.setPriority(debt.getPriority());
        dto.setNotes(debt.getNotes());
        
        if (debt.getStartDate() != null) {
            dto.setStartDate(debt.getStartDate().toLocalDateTime());
        }
        if (debt.getEndDate() != null) {
            dto.setEndDate(debt.getEndDate().toLocalDateTime());
        }
        if (debt.getCreatedAt() != null) {
            dto.setCreatedAt(debt.getCreatedAt().toLocalDateTime());
        }
        if (debt.getUpdatedAt() != null) {
            dto.setUpdatedAt(debt.getUpdatedAt().toLocalDateTime());
        }
        
        return dto;
    }
    
    private DebtPaymentDTO convertPaymentToDTO(DebtPayment payment) {
        DebtPaymentDTO dto = new DebtPaymentDTO();
        dto.setId(payment.getId());
        dto.setDebtId(payment.getDebtId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentType(payment.getPaymentType());
        dto.setNotes(payment.getNotes());
        
        if (payment.getPaymentDate() != null) {
            dto.setPaymentDate(payment.getPaymentDate().toLocalDateTime());
        }
        if (payment.getCreatedAt() != null) {
            dto.setCreatedAt(payment.getCreatedAt().toLocalDateTime());
        }
        if (payment.getUpdatedAt() != null) {
            dto.setUpdatedAt(payment.getUpdatedAt().toLocalDateTime());
        }
        
        return dto;
    }
} 