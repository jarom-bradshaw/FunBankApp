package com.jarom.funbankapp.controller;

import com.jarom.funbankapp.dto.ApiResponse;
import com.jarom.funbankapp.dto.UserDTO;
import com.jarom.funbankapp.dto.UserUpdateRequest;
import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.UserRepository;
import com.jarom.funbankapp.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User profile management")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    public UserController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("/profile")
    @Operation(
        summary = "Get user profile", 
        description = "Retrieves the profile information for the authenticated user."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Profile retrieved successfully",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<ApiResponse<UserDTO>> getProfile(Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setUsername(user.getUsername());
            userDTO.setEmail(user.getEmail());
            userDTO.setFirstName(user.getFirstName());
            userDTO.setLastName(user.getLastName());
            userDTO.setCreatedAt(user.getCreatedAt() != null ? 
                user.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() : null);

            return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", userDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve profile: " + e.getMessage()));
        }
    }

    @PutMapping("/profile")
    @Operation(
        summary = "Update user profile (full update)", 
        description = "Updates the complete profile information for the authenticated user. All fields must be provided."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Profile updated successfully",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid profile data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<ApiResponse<UserDTO>> updateProfile(
        @Parameter(description = "Complete profile data to update", required = true)
        @Valid @RequestBody UserDTO request, 
        Authentication authentication
    ) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setEmail(request.getEmail());

            userRepository.updateUser(user);

            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setUsername(user.getUsername());
            userDTO.setEmail(user.getEmail());
            userDTO.setFirstName(user.getFirstName());
            userDTO.setLastName(user.getLastName());
            userDTO.setCreatedAt(user.getCreatedAt() != null ? 
                user.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() : null);

            return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", userDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to update profile: " + e.getMessage()));
        }
    }

    @PatchMapping("/profile")
    @Operation(
        summary = "Partially update user profile", 
        description = "Updates specific fields of the authenticated user's profile. Only provided fields will be updated."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Profile partially updated successfully",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid profile data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<ApiResponse<UserDTO>> patchProfile(
        @Parameter(description = "Partial profile data to update", required = true)
        @Valid @RequestBody UserUpdateRequest request, 
        Authentication authentication
    ) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Apply partial updates
            if (request.getFirstName() != null) {
                user.setFirstName(request.getFirstName());
            }
            if (request.getLastName() != null) {
                user.setLastName(request.getLastName());
            }
            if (request.getEmail() != null) {
                user.setEmail(request.getEmail());
            }

            userRepository.updateUser(user);

            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setUsername(user.getUsername());
            userDTO.setEmail(user.getEmail());
            userDTO.setFirstName(user.getFirstName());
            userDTO.setLastName(user.getLastName());
            userDTO.setCreatedAt(user.getCreatedAt() != null ? 
                user.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() : null);

            return ResponseEntity.ok(ApiResponse.success("Profile partially updated successfully", userDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to update profile: " + e.getMessage()));
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserByUsername(@PathVariable String username) {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setUsername(user.getUsername());
            userDTO.setEmail(user.getEmail());
            userDTO.setFirstName(user.getFirstName());
            userDTO.setLastName(user.getLastName());
            userDTO.setCreatedAt(user.getCreatedAt() != null ? 
                user.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() : null);

            return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", userDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve user: " + e.getMessage()));
        }
    }

    @DeleteMapping("/account")
    @Operation(
        summary = "Delete user account", 
        description = "Permanently deletes the authenticated user's account and all associated data (accounts, transactions, budgets, goals). This action is irreversible."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "User account deleted successfully",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "User has accounts with balance"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<ApiResponse<String>> deleteUserAccount(Authentication authentication) {
        try {
            String username = authentication.getName();
            userService.deleteUserAccount(username);
            return ResponseEntity.ok(ApiResponse.success("User account deleted successfully", 
                "Your account and all associated data have been permanently deleted"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to delete user account: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{username}")
    @Operation(
        summary = "Delete user by username (Admin only)", 
        description = "Permanently deletes a user account and all associated data. This action is irreversible and should only be used by administrators."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "User deleted successfully",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "User has accounts with balance"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Unauthorized: Admin access required"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ApiResponse<String>> deleteUserByUsername(
            @Parameter(description = "Username to delete", example = "john_doe")
            @PathVariable String username,
            Authentication authentication) {
        try {
            // Check if current user is admin (you can implement admin role checking)
            String currentUsername = authentication.getName();
            if (!currentUsername.equals("admin")) { // Simple admin check - you might want to implement proper role-based auth
                return ResponseEntity.status(403).body(ApiResponse.error("Unauthorized: Admin access required"));
            }
            
            userService.deleteUserAccount(username);
            return ResponseEntity.ok(ApiResponse.success("User deleted successfully", 
                "User '" + username + "' and all associated data have been permanently deleted"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to delete user: " + e.getMessage()));
        }
    }
}
