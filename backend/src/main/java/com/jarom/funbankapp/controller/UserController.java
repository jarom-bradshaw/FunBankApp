package com.jarom.funbankapp.controller;

import com.jarom.funbankapp.security.JwtService;
import com.jarom.funbankapp.model.LoginRequest;
import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.UserDAO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/users")
@io.swagger.v3.oas.annotations.tags.Tag(name = "Users", description = "User management operations")
public class UserController {

    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserController(UserDAO userDAO, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Creates a new user account.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User registered successfully"),
        @ApiResponse(responseCode = "400", description = "Username already exists")
    })
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            System.out.println("🔐 Registration attempt for user: " + user.getUsername());
            
            if (userDAO.existsByUsername(user.getUsername())) {
                System.out.println("❌ Username already exists: " + user.getUsername());
                return ResponseEntity.badRequest().body("Username already exists.");
            }

            String hashedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(hashedPassword);
            System.out.println("🔑 Password hashed successfully");

            int result = userDAO.save(user);
            System.out.println("✅ User saved successfully with result: " + result);
            
            return ResponseEntity.ok("User registered successfully!");
        } catch (Exception e) {
            System.err.println("❌ Registration error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates user and returns JWT token.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login successful"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            User user = userDAO.findByUsername(request.getUsername());

            if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                String token = jwtService.generateToken(user.getUsername());
                System.out.println("🔑 Generated token: " + token.substring(0, Math.min(20, token.length())) + "...");
                return ResponseEntity.ok().body("Bearer " + token);
            } else {
                return ResponseEntity.status(401).body("Invalid credentials");
            }
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

    @GetMapping("/profile")
    @Operation(summary = "Get user profile", description = "Retrieves the current user's profile information.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<?> getProfile() {
        String username = getCurrentUsername();
        User user = userDAO.findByUsername(username);
        
        // Return only safe profile data (no password)
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getId());
        profile.put("username", user.getUsername());
        profile.put("email", user.getEmail());
        
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/profile")
    @Operation(summary = "Update user profile", description = "Updates the current user's profile information.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid profile data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<?> updateProfile(@RequestBody User request) {
        String username = getCurrentUsername();
        User currentUser = userDAO.findByUsername(username);
        
        // Update only allowed fields
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            currentUser.setEmail(request.getEmail());
        }
        
        userDAO.updateUser(currentUser);
        return ResponseEntity.ok("Profile updated successfully!");
    }

    @PutMapping("/password")
    @Operation(summary = "Change password", description = "Changes the current user's password.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Password changed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid password data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeRequest request) {
        String username = getCurrentUsername();
        User currentUser = userDAO.findByUsername(username);
        
        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), currentUser.getPassword())) {
            return ResponseEntity.badRequest().body("Current password is incorrect.");
        }
        
        // Update password
        String newHashedPassword = passwordEncoder.encode(request.getNewPassword());
        currentUser.setPassword(newHashedPassword);
        
        userDAO.updateUser(currentUser);
        return ResponseEntity.ok("Password changed successfully!");
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Logs out the current user (client should discard JWT token).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Logout successful")
    })
    public ResponseEntity<?> logout() {
        // In a stateless JWT system, logout is handled client-side by discarding the token
        // This endpoint provides a clear logout action for the client
        return ResponseEntity.ok("Logout successful. Please discard your authentication token.");
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    // Inner class for password change request
    public static class PasswordChangeRequest {
        private String currentPassword;
        private String newPassword;

        public String getCurrentPassword() { return currentPassword; }
        public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}
