package com.jarom.funbankapp.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jarom.funbankapp.dto.DebtReminderDTO;
import com.jarom.funbankapp.exception.ResourceNotFoundException;
import com.jarom.funbankapp.exception.UnauthorizedException;
import com.jarom.funbankapp.model.Debt;
import com.jarom.funbankapp.model.DebtReminder;
import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.DebtReminderRepository;
import com.jarom.funbankapp.repository.DebtRepository;
import com.jarom.funbankapp.repository.UserRepository;

@Service
public class DebtReminderServiceImpl implements DebtReminderService {

    private final DebtReminderRepository debtReminderRepository;
    private final DebtRepository debtRepository;
    private final UserRepository userRepository;

    public DebtReminderServiceImpl(DebtReminderRepository debtReminderRepository, DebtRepository debtRepository, UserRepository userRepository) {
        this.debtReminderRepository = debtReminderRepository;
        this.debtRepository = debtRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public DebtReminderDTO createReminder(DebtReminderDTO reminderDTO) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        // Verify user owns the debt
        Debt debt = debtRepository.findById(reminderDTO.getDebtId())
                .orElseThrow(() -> new ResourceNotFoundException("Debt", "id", reminderDTO.getDebtId()));
        
        if (!debt.getUserId().equals(user.getId())) {
            throw new UnauthorizedException("You don't own this debt");
        }
        
        // Validate reminder data
        validateReminderData(reminderDTO);
        
        // Create reminder model
        DebtReminder reminder = new DebtReminder();
        reminder.setDebtId(reminderDTO.getDebtId());
        reminder.setReminderDate(reminderDTO.getReminderDate() != null ? 
            Timestamp.valueOf(reminderDTO.getReminderDate()) : null);
        reminder.setDueDate(reminderDTO.getDueDate() != null ? 
            Timestamp.valueOf(reminderDTO.getDueDate()) : null);
        reminder.setAmount(reminderDTO.getAmount());
        reminder.setReminderType(reminderDTO.getReminderType() != null ? reminderDTO.getReminderType() : "email");
        reminder.setIsSent(reminderDTO.getIsSent() != null ? reminderDTO.getIsSent() : false);
        reminder.setNotes(reminderDTO.getNotes());
        reminder.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        reminder.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        
        // Save to database
        DebtReminder savedReminder = debtReminderRepository.save(reminder);
        
        return convertToDTO(savedReminder);
    }

    @Override
    public List<DebtReminderDTO> getUserReminders() {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        // Get all user's debts
        List<Debt> userDebts = debtRepository.findByUserId(user.getId());
        List<Long> debtIds = userDebts.stream().map(Debt::getId).collect(Collectors.toList());
        
        // Get all reminders for user's debts
        List<DebtReminder> reminders = debtReminderRepository.findAll().stream()
                .filter(reminder -> debtIds.contains(reminder.getDebtId()))
                .collect(Collectors.toList());
        
        return reminders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DebtReminderDTO> getRemindersByDebt(Long debtId) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        // Verify user owns the debt
        Debt debt = debtRepository.findById(debtId)
                .orElseThrow(() -> new ResourceNotFoundException("Debt", "id", debtId));
        
        if (!debt.getUserId().equals(user.getId())) {
            throw new UnauthorizedException("You don't own this debt");
        }
        
        List<DebtReminder> reminders = debtReminderRepository.findByDebtId(debtId);
        return reminders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DebtReminderDTO getReminderById(Long reminderId) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        DebtReminder reminder = debtReminderRepository.findById(reminderId)
                .orElseThrow(() -> new ResourceNotFoundException("DebtReminder", "id", reminderId));
        
        // Verify user owns the debt
        Debt debt = debtRepository.findById(reminder.getDebtId())
                .orElseThrow(() -> new ResourceNotFoundException("Debt", "id", reminder.getDebtId()));
        
        if (!debt.getUserId().equals(user.getId())) {
            throw new UnauthorizedException("You don't own this reminder");
        }
        
        return convertToDTO(reminder);
    }

    @Override
    @Transactional
    public DebtReminderDTO updateReminder(Long reminderId, DebtReminderDTO reminderDTO) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        DebtReminder reminder = debtReminderRepository.findById(reminderId)
                .orElseThrow(() -> new ResourceNotFoundException("DebtReminder", "id", reminderId));
        
        // Verify user owns the debt
        Debt debt = debtRepository.findById(reminder.getDebtId())
                .orElseThrow(() -> new ResourceNotFoundException("Debt", "id", reminder.getDebtId()));
        
        if (!debt.getUserId().equals(user.getId())) {
            throw new UnauthorizedException("You don't own this reminder");
        }
        
        // Validate reminder data
        validateReminderData(reminderDTO);
        
        // Update fields
        if (reminderDTO.getReminderDate() != null) {
            reminder.setReminderDate(Timestamp.valueOf(reminderDTO.getReminderDate()));
        }
        if (reminderDTO.getDueDate() != null) {
            reminder.setDueDate(Timestamp.valueOf(reminderDTO.getDueDate()));
        }
        if (reminderDTO.getAmount() != null) {
            reminder.setAmount(reminderDTO.getAmount());
        }
        if (reminderDTO.getReminderType() != null) {
            reminder.setReminderType(reminderDTO.getReminderType());
        }
        if (reminderDTO.getNotes() != null) {
            reminder.setNotes(reminderDTO.getNotes());
        }
        
        reminder.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        
        // Save to database
        DebtReminder updatedReminder = debtReminderRepository.save(reminder);
        
        return convertToDTO(updatedReminder);
    }

    @Override
    @Transactional
    public void deleteReminder(Long reminderId) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        DebtReminder reminder = debtReminderRepository.findById(reminderId)
                .orElseThrow(() -> new ResourceNotFoundException("DebtReminder", "id", reminderId));
        
        // Verify user owns the debt
        Debt debt = debtRepository.findById(reminder.getDebtId())
                .orElseThrow(() -> new ResourceNotFoundException("Debt", "id", reminder.getDebtId()));
        
        if (!debt.getUserId().equals(user.getId())) {
            throw new UnauthorizedException("You don't own this reminder");
        }
        
        debtReminderRepository.deleteById(reminderId);
    }

    @Override
    @Transactional
    public DebtReminderDTO markReminderAsSent(Long reminderId) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        DebtReminder reminder = debtReminderRepository.findById(reminderId)
                .orElseThrow(() -> new ResourceNotFoundException("DebtReminder", "id", reminderId));
        
        // Verify user owns the debt
        Debt debt = debtRepository.findById(reminder.getDebtId())
                .orElseThrow(() -> new ResourceNotFoundException("Debt", "id", reminder.getDebtId()));
        
        if (!debt.getUserId().equals(user.getId())) {
            throw new UnauthorizedException("You don't own this reminder");
        }
        
        reminder.setIsSent(true);
        reminder.setSentDate(new Timestamp(System.currentTimeMillis()));
        reminder.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        
        DebtReminder updatedReminder = debtReminderRepository.save(reminder);
        
        return convertToDTO(updatedReminder);
    }

    @Override
    public List<DebtReminderDTO> getUpcomingReminders(int days) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        // Get all user's debts
        List<Debt> userDebts = debtRepository.findByUserId(user.getId());
        List<Long> debtIds = userDebts.stream().map(Debt::getId).collect(Collectors.toList());
        
        // Calculate date range
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDate = now.plusDays(days);
        
        // Get reminders within date range
        List<DebtReminder> reminders = debtReminderRepository.findAll().stream()
                .filter(reminder -> debtIds.contains(reminder.getDebtId()))
                .filter(reminder -> reminder.getDueDate() != null)
                .filter(reminder -> {
                    LocalDateTime dueDate = reminder.getDueDate().toLocalDateTime();
                    return dueDate.isAfter(now) && dueDate.isBefore(endDate);
                })
                .collect(Collectors.toList());
        
        return reminders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DebtReminderDTO> getOverdueReminders() {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        // Get all user's debts
        List<Debt> userDebts = debtRepository.findByUserId(user.getId());
        List<Long> debtIds = userDebts.stream().map(Debt::getId).collect(Collectors.toList());
        
        LocalDateTime now = LocalDateTime.now();
        
        // Get overdue reminders
        List<DebtReminder> reminders = debtReminderRepository.findAll().stream()
                .filter(reminder -> debtIds.contains(reminder.getDebtId()))
                .filter(reminder -> reminder.getDueDate() != null)
                .filter(reminder -> reminder.getDueDate().toLocalDateTime().isBefore(now))
                .collect(Collectors.toList());
        
        return reminders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public int generateAutomaticReminders() {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        List<Debt> userDebts = debtRepository.findByUserId(user.getId());
        int remindersCreated = 0;
        
        for (Debt debt : userDebts) {
            if (debt.getCurrentBalance().compareTo(java.math.BigDecimal.ZERO) > 0 && debt.getDueDate() != null) {
                // Create reminder for next payment due date
                LocalDateTime nextDueDate = calculateNextDueDate(debt.getDueDate());
                
                DebtReminder reminder = new DebtReminder();
                reminder.setDebtId(debt.getId());
                reminder.setDueDate(Timestamp.valueOf(nextDueDate));
                reminder.setReminderDate(Timestamp.valueOf(nextDueDate.minusDays(3))); // 3 days before
                reminder.setAmount(debt.getMinimumPayment());
                reminder.setReminderType("email");
                reminder.setIsSent(false);
                reminder.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                reminder.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                
                debtReminderRepository.save(reminder);
                remindersCreated++;
            }
        }
        
        return remindersCreated;
    }

    @Override
    public Map<String, Object> getReminderSummary() {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        // Get all user's debts
        List<Debt> userDebts = debtRepository.findByUserId(user.getId());
        List<Long> debtIds = userDebts.stream().map(Debt::getId).collect(Collectors.toList());
        
        // Get all reminders for user's debts
        List<DebtReminder> reminders = debtReminderRepository.findAll().stream()
                .filter(reminder -> debtIds.contains(reminder.getDebtId()))
                .collect(Collectors.toList());
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalReminders", reminders.size());
        summary.put("sentReminders", reminders.stream().filter(DebtReminder::getIsSent).count());
        summary.put("pendingReminders", reminders.stream().filter(r -> !r.getIsSent()).count());
        summary.put("overdueReminders", getOverdueReminders().size());
        summary.put("upcomingReminders", getUpcomingReminders(7).size());
        
        return summary;
    }

    @Override
    public boolean userOwnsReminder(Long reminderId) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        return debtReminderRepository.findById(reminderId)
                .map(reminder -> {
                    try {
                        Debt debt = debtRepository.findById(reminder.getDebtId())
                                .orElse(null);
                        return debt != null && debt.getUserId().equals(user.getId());
                    } catch (Exception e) {
                        return false;
                    }
                })
                .orElse(false);
    }

    @Override
    @Transactional
    public DebtReminderDTO enableReminder(Long reminderId) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        DebtReminder reminder = debtReminderRepository.findById(reminderId)
                .orElseThrow(() -> new ResourceNotFoundException("DebtReminder", "id", reminderId));
        
        // Verify user owns the debt
        Debt debt = debtRepository.findById(reminder.getDebtId())
                .orElseThrow(() -> new ResourceNotFoundException("Debt", "id", reminder.getDebtId()));
        
        if (!debt.getUserId().equals(user.getId())) {
            throw new UnauthorizedException("You don't own this reminder");
        }
        
        reminder.setIsActive(true);
        reminder.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        
        DebtReminder updatedReminder = debtReminderRepository.save(reminder);
        
        return convertToDTO(updatedReminder);
    }

    @Override
    @Transactional
    public DebtReminderDTO disableReminder(Long reminderId) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        DebtReminder reminder = debtReminderRepository.findById(reminderId)
                .orElseThrow(() -> new ResourceNotFoundException("DebtReminder", "id", reminderId));
        
        // Verify user owns the debt
        Debt debt = debtRepository.findById(reminder.getDebtId())
                .orElseThrow(() -> new ResourceNotFoundException("Debt", "id", reminder.getDebtId()));
        
        if (!debt.getUserId().equals(user.getId())) {
            throw new UnauthorizedException("You don't own this reminder");
        }
        
        reminder.setIsActive(false);
        reminder.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        
        DebtReminder updatedReminder = debtReminderRepository.save(reminder);
        
        return convertToDTO(updatedReminder);
    }

    @Override
    public List<DebtReminderDTO> getActiveReminders() {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        // Get all user's debts
        List<Debt> userDebts = debtRepository.findByUserId(user.getId());
        List<Long> debtIds = userDebts.stream().map(Debt::getId).collect(Collectors.toList());
        
        // Get active reminders
        List<DebtReminder> reminders = debtReminderRepository.findAll().stream()
                .filter(reminder -> debtIds.contains(reminder.getDebtId()))
                .filter(DebtReminder::getIsActive)
                .collect(Collectors.toList());
        
        return reminders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DebtReminderDTO> getUpcomingReminders() {
        // Default to 7 days
        return getUpcomingReminders(7);
    }

    @Override
    @Transactional
    public List<DebtReminderDTO> generateReminders() {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        List<Debt> userDebts = debtRepository.findByUserId(user.getId());
        List<DebtReminderDTO> generatedReminders = new ArrayList<>();
        
        for (Debt debt : userDebts) {
            if (debt.getCurrentBalance().compareTo(java.math.BigDecimal.ZERO) > 0 && debt.getDueDate() != null) {
                // Create reminder for next payment due date
                LocalDateTime nextDueDate = calculateNextDueDate(debt.getDueDate());
                
                DebtReminder reminder = new DebtReminder();
                reminder.setDebtId(debt.getId());
                reminder.setDueDate(Timestamp.valueOf(nextDueDate));
                reminder.setReminderDate(Timestamp.valueOf(nextDueDate.minusDays(3))); // 3 days before
                reminder.setAmount(debt.getMinimumPayment());
                reminder.setReminderType("email");
                reminder.setIsSent(false);
                reminder.setIsActive(true);
                reminder.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                reminder.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                
                DebtReminder savedReminder = debtReminderRepository.save(reminder);
                generatedReminders.add(convertToDTO(savedReminder));
            }
        }
        
        return generatedReminders;
    }

    @Override
    @Transactional
    public DebtReminderDTO snoozeReminder(Long reminderId, Integer snoozeDays) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        DebtReminder reminder = debtReminderRepository.findById(reminderId)
                .orElseThrow(() -> new ResourceNotFoundException("DebtReminder", "id", reminderId));
        
        // Verify user owns the debt
        Debt debt = debtRepository.findById(reminder.getDebtId())
                .orElseThrow(() -> new ResourceNotFoundException("Debt", "id", reminder.getDebtId()));
        
        if (!debt.getUserId().equals(user.getId())) {
            throw new UnauthorizedException("You don't own this reminder");
        }
        
        // Snooze the reminder by updating the reminder date
        LocalDateTime currentReminderDate = reminder.getReminderDate().toLocalDateTime();
        LocalDateTime newReminderDate = currentReminderDate.plusDays(snoozeDays);
        
        reminder.setReminderDate(Timestamp.valueOf(newReminderDate));
        reminder.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        
        DebtReminder updatedReminder = debtReminderRepository.save(reminder);
        
        return convertToDTO(updatedReminder);
    }

    // Helper methods
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
    
    private void validateReminderData(DebtReminderDTO reminderDTO) {
        if (reminderDTO.getDebtId() == null) {
            throw new IllegalArgumentException("Debt ID is required");
        }
        if (reminderDTO.getDueDate() == null) {
            throw new IllegalArgumentException("Due date is required");
        }
        if (reminderDTO.getAmount() == null || reminderDTO.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
    }
    
    private LocalDateTime calculateNextDueDate(Integer dueDay) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextDue = now.withDayOfMonth(dueDay);
        
        if (nextDue.isBefore(now)) {
            nextDue = nextDue.plusMonths(1);
        }
        
        return nextDue;
    }
    
    private DebtReminderDTO convertToDTO(DebtReminder reminder) {
        DebtReminderDTO dto = new DebtReminderDTO();
        dto.setId(reminder.getId());
        dto.setDebtId(reminder.getDebtId());
        dto.setAmount(reminder.getAmount());
        dto.setReminderType(reminder.getReminderType());
        dto.setIsSent(reminder.getIsSent());
        dto.setNotes(reminder.getNotes());
        
        if (reminder.getReminderDate() != null) {
            dto.setReminderDate(reminder.getReminderDate().toLocalDateTime());
        }
        if (reminder.getDueDate() != null) {
            dto.setDueDate(reminder.getDueDate().toLocalDateTime());
        }
        if (reminder.getSentDate() != null) {
            dto.setSentDate(reminder.getSentDate().toLocalDateTime());
        }
        if (reminder.getCreatedAt() != null) {
            dto.setCreatedAt(reminder.getCreatedAt().toLocalDateTime());
        }
        if (reminder.getUpdatedAt() != null) {
            dto.setUpdatedAt(reminder.getUpdatedAt().toLocalDateTime());
        }
        
        return dto;
    }
} 