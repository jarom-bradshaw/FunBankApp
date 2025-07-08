package com.jarom.funbankapp.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jarom.funbankapp.dto.DebtReminderDTO;
import com.jarom.funbankapp.service.DebtReminderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/debt-reminders")
@Tag(name = "Debt Reminders", description = "Debt payment reminder management")
@SecurityRequirement(name = "bearerAuth")
public class DebtReminderController {

    private final DebtReminderService debtReminderService;

    public DebtReminderController(DebtReminderService debtReminderService) {
        this.debtReminderService = debtReminderService;
    }

    @PostMapping
    @Operation(
        summary = "Create a new debt reminder", 
        description = "Creates a new debt payment reminder for the authenticated user."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Reminder created successfully",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid reminder data provided"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<com.jarom.funbankapp.dto.ApiResponse<DebtReminderDTO>> createReminder(
        @Parameter(description = "Reminder details to create", required = true)
        @Valid @RequestBody DebtReminderDTO request
    ) {
        DebtReminderDTO createdReminder = debtReminderService.createReminder(request);
        return ResponseEntity.ok(com.jarom.funbankapp.dto.ApiResponse.success("Reminder created successfully", createdReminder));
    }

    @GetMapping
    @Operation(
        summary = "Get user reminders", 
        description = "Retrieves all debt payment reminders for the authenticated user."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Reminders retrieved successfully",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<com.jarom.funbankapp.dto.ApiResponse<List<DebtReminderDTO>>> getUserReminders() {
        List<DebtReminderDTO> reminders = debtReminderService.getUserReminders();
        return ResponseEntity.ok(com.jarom.funbankapp.dto.ApiResponse.success("Reminders retrieved successfully", reminders));
    }

    @GetMapping("/{reminderId}")
    @Operation(
        summary = "Get reminder by ID", 
        description = "Retrieves a specific debt reminder by ID. The reminder must belong to the authenticated user."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Reminder retrieved successfully",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Unauthorized: You don't own this reminder"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Reminder not found")
    })
    public ResponseEntity<com.jarom.funbankapp.dto.ApiResponse<DebtReminderDTO>> getReminderById(
        @Parameter(description = "Reminder ID to retrieve", example = "1")
        @PathVariable Long reminderId
    ) {
        DebtReminderDTO reminder = debtReminderService.getReminderById(reminderId);
        return ResponseEntity.ok(com.jarom.funbankapp.dto.ApiResponse.success("Reminder retrieved successfully", reminder));
    }

    @PutMapping("/{reminderId}")
    @Operation(
        summary = "Update reminder", 
        description = "Updates an existing debt reminder. The reminder must belong to the authenticated user."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Reminder updated successfully",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid reminder data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Unauthorized: You don't own this reminder"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Reminder not found")
    })
    public ResponseEntity<com.jarom.funbankapp.dto.ApiResponse<DebtReminderDTO>> updateReminder(
        @Parameter(description = "Reminder ID to update", example = "1")
        @PathVariable Long reminderId,
        @Parameter(description = "Updated reminder data", required = true)
        @Valid @RequestBody DebtReminderDTO request
    ) {
        DebtReminderDTO updatedReminder = debtReminderService.updateReminder(reminderId, request);
        return ResponseEntity.ok(com.jarom.funbankapp.dto.ApiResponse.success("Reminder updated successfully", updatedReminder));
    }

    @DeleteMapping("/{reminderId}")
    @Operation(
        summary = "Delete reminder", 
        description = "Deletes a debt reminder. The reminder must belong to the authenticated user."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Reminder deleted successfully",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Unauthorized: You don't own this reminder"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Reminder not found")
    })
    public ResponseEntity<com.jarom.funbankapp.dto.ApiResponse<String>> deleteReminder(
        @Parameter(description = "Reminder ID to delete", example = "1")
        @PathVariable Long reminderId
    ) {
        debtReminderService.deleteReminder(reminderId);
        return ResponseEntity.ok(com.jarom.funbankapp.dto.ApiResponse.success("Reminder deleted successfully", 
            "Reminder with ID " + reminderId + " has been deleted"));
    }

    @PostMapping("/{reminderId}/enable")
    @Operation(
        summary = "Enable reminder", 
        description = "Enables a debt reminder that was previously disabled."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Reminder enabled successfully",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Unauthorized: You don't own this reminder"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Reminder not found")
    })
    public ResponseEntity<com.jarom.funbankapp.dto.ApiResponse<DebtReminderDTO>> enableReminder(
        @Parameter(description = "Reminder ID to enable", example = "1")
        @PathVariable Long reminderId
    ) {
        DebtReminderDTO enabledReminder = debtReminderService.enableReminder(reminderId);
        return ResponseEntity.ok(com.jarom.funbankapp.dto.ApiResponse.success("Reminder enabled successfully", enabledReminder));
    }

    @PostMapping("/{reminderId}/disable")
    @Operation(
        summary = "Disable reminder", 
        description = "Disables a debt reminder temporarily."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Reminder disabled successfully",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Unauthorized: You don't own this reminder"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Reminder not found")
    })
    public ResponseEntity<com.jarom.funbankapp.dto.ApiResponse<DebtReminderDTO>> disableReminder(
        @Parameter(description = "Reminder ID to disable", example = "1")
        @PathVariable Long reminderId
    ) {
        DebtReminderDTO disabledReminder = debtReminderService.disableReminder(reminderId);
        return ResponseEntity.ok(com.jarom.funbankapp.dto.ApiResponse.success("Reminder disabled successfully", disabledReminder));
    }

    @GetMapping("/active")
    @Operation(
        summary = "Get active reminders", 
        description = "Retrieves all active debt reminders for the authenticated user."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Active reminders retrieved successfully",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<com.jarom.funbankapp.dto.ApiResponse<List<DebtReminderDTO>>> getActiveReminders() {
        List<DebtReminderDTO> activeReminders = debtReminderService.getActiveReminders();
        return ResponseEntity.ok(com.jarom.funbankapp.dto.ApiResponse.success("Active reminders retrieved successfully", activeReminders));
    }

    @GetMapping("/upcoming")
    @Operation(
        summary = "Get upcoming reminders", 
        description = "Retrieves upcoming debt payment reminders for the authenticated user."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Upcoming reminders retrieved successfully",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<com.jarom.funbankapp.dto.ApiResponse<List<DebtReminderDTO>>> getUpcomingReminders() {
        List<DebtReminderDTO> upcomingReminders = debtReminderService.getUpcomingReminders();
        return ResponseEntity.ok(com.jarom.funbankapp.dto.ApiResponse.success("Upcoming reminders retrieved successfully", upcomingReminders));
    }

    @GetMapping("/by-debt/{debtId}")
    @Operation(
        summary = "Get reminders by debt", 
        description = "Retrieves all reminders for a specific debt. The debt must belong to the authenticated user."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Reminders retrieved successfully",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Unauthorized: You don't own this debt"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Debt not found")
    })
    public ResponseEntity<com.jarom.funbankapp.dto.ApiResponse<List<DebtReminderDTO>>> getRemindersByDebt(
        @Parameter(description = "Debt ID to get reminders for", example = "1")
        @PathVariable Long debtId
    ) {
        List<DebtReminderDTO> reminders = debtReminderService.getRemindersByDebt(debtId);
        return ResponseEntity.ok(com.jarom.funbankapp.dto.ApiResponse.success("Reminders retrieved successfully", reminders));
    }

    @PostMapping("/generate")
    @Operation(
        summary = "Generate reminders", 
        description = "Automatically generates payment reminders for all user debts based on their payment schedules."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Reminders generated successfully",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<com.jarom.funbankapp.dto.ApiResponse<List<DebtReminderDTO>>> generateReminders() {
        List<DebtReminderDTO> generatedReminders = debtReminderService.generateReminders();
        return ResponseEntity.ok(com.jarom.funbankapp.dto.ApiResponse.success("Reminders generated successfully", generatedReminders));
    }

    @PostMapping("/{reminderId}/snooze")
    @Operation(
        summary = "Snooze reminder", 
        description = "Snoozes a reminder for a specified number of days."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Reminder snoozed successfully",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid snooze duration"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Unauthorized: You don't own this reminder"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Reminder not found")
    })
    public ResponseEntity<com.jarom.funbankapp.dto.ApiResponse<DebtReminderDTO>> snoozeReminder(
        @Parameter(description = "Reminder ID to snooze", example = "1")
        @PathVariable Long reminderId,
        @Parameter(description = "Number of days to snooze", example = "3")
        @RequestBody Integer snoozeDays
    ) {
        DebtReminderDTO snoozedReminder = debtReminderService.snoozeReminder(reminderId, snoozeDays);
        return ResponseEntity.ok(com.jarom.funbankapp.dto.ApiResponse.success("Reminder snoozed successfully", snoozedReminder));
    }
} 