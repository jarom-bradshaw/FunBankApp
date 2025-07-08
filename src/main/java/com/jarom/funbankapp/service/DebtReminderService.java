package com.jarom.funbankapp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.jarom.funbankapp.dto.DebtReminderDTO;

/**
 * Service layer for debt reminder operations
 * Handles business logic for debt payment reminders
 */
public interface DebtReminderService {
    
    /**
     * Create a new reminder for a debt
     * @param reminderDTO reminder data transfer object
     * @return created reminder DTO
     */
    DebtReminderDTO createReminder(DebtReminderDTO reminderDTO);
    
    /**
     * Get all reminders for the authenticated user
     * @return list of reminder DTOs
     */
    List<DebtReminderDTO> getUserReminders();
    
    /**
     * Get reminders for a specific debt
     * @param debtId the debt ID
     * @return list of reminder DTOs
     */
    List<DebtReminderDTO> getRemindersByDebt(Long debtId);
    
    /**
     * Get a specific reminder by ID (if user owns the debt)
     * @param reminderId the reminder ID
     * @return reminder DTO
     */
    DebtReminderDTO getReminderById(Long reminderId);
    
    /**
     * Update an existing reminder (if user owns the debt)
     * @param reminderId the reminder ID
     * @param reminderDTO updated reminder data
     * @return updated reminder DTO
     */
    DebtReminderDTO updateReminder(Long reminderId, DebtReminderDTO reminderDTO);
    
    /**
     * Delete a reminder (if user owns the debt)
     * @param reminderId the reminder ID
     */
    void deleteReminder(Long reminderId);
    
    /**
     * Mark a reminder as sent
     * @param reminderId the reminder ID
     * @return updated reminder DTO
     */
    DebtReminderDTO markReminderAsSent(Long reminderId);
    
    /**
     * Get upcoming reminders
     * @param days number of days to look ahead
     * @return list of upcoming reminder DTOs
     */
    List<DebtReminderDTO> getUpcomingReminders(int days);
    
    /**
     * Get overdue reminders
     * @return list of overdue reminder DTOs
     */
    List<DebtReminderDTO> getOverdueReminders();
    
    /**
     * Generate automatic reminders for all user debts
     * @return number of reminders created
     */
    int generateAutomaticReminders();
    
    /**
     * Get reminder summary for the user
     * @return reminder summary data
     */
    Map<String, Object> getReminderSummary();
    
    /**
     * Check if user owns the reminder
     * @param reminderId the reminder ID
     * @return true if user owns the reminder
     */
    boolean userOwnsReminder(Long reminderId);
    
    /**
     * Enable a reminder
     * @param reminderId the reminder ID
     * @return updated reminder DTO
     */
    DebtReminderDTO enableReminder(Long reminderId);
    
    /**
     * Disable a reminder
     * @param reminderId the reminder ID
     * @return updated reminder DTO
     */
    DebtReminderDTO disableReminder(Long reminderId);
    
    /**
     * Get active reminders for the user
     * @return list of active reminder DTOs
     */
    List<DebtReminderDTO> getActiveReminders();
    
    /**
     * Get upcoming reminders (default 7 days)
     * @return list of upcoming reminder DTOs
     */
    List<DebtReminderDTO> getUpcomingReminders();
    
    /**
     * Generate reminders for all user debts
     * @return list of generated reminder DTOs
     */
    List<DebtReminderDTO> generateReminders();
    
    /**
     * Snooze a reminder for specified days
     * @param reminderId the reminder ID
     * @param snoozeDays number of days to snooze
     * @return updated reminder DTO
     */
    DebtReminderDTO snoozeReminder(Long reminderId, Integer snoozeDays);
} 