package com.jarom.funbankapp.controller;

import com.jarom.funbankapp.dto.LoginRequest;
import com.jarom.funbankapp.dto.UserDTO;
import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.UserDAO;
import com.jarom.funbankapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService, UserDAO userDAO, 
                        PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserDTO userDTO) {
        try {
            // Check if user already exists
            if (userDAO.existsByEmail(userDTO.getEmail())) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "User with this email already exists");
                return ResponseEntity.badRequest().body(response);
            }

            // Create new user
            User user = new User();
            user.setUsername(userDTO.getUsername());
            user.setEmail(userDTO.getEmail());
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            user.setFirstName(userDTO.getFirstName());
            user.setLastName(userDTO.getLastName());

            userDAO.save(user);

            Map<String, String> response = new HashMap<>();
            response.put("message", "User registered successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Registration failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, 
                                  HttpServletRequest request) {
        try {
            System.out.println("🔐 Login attempt for email: " + loginRequest.getEmail());
            
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            System.out.println("✅ Authentication successful for: " + loginRequest.getEmail());

            // Set authentication in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Create session
            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            // Get user details
            User user = userDAO.findByEmail(loginRequest.getEmail());
            System.out.println("👤 User found: " + user.getEmail() + " (ID: " + user.getId() + ")");
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("user", Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "firstName", user.getFirstName(),
                "lastName", user.getLastName()
            ));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("❌ Login failed for " + loginRequest.getEmail() + ": " + e.getMessage());
            e.printStackTrace();
            Map<String, String> response = new HashMap<>();
            response.put("error", "Invalid credentials");
            return ResponseEntity.status(401).body(response);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        try {
            // Invalidate session
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }

            // Clear security context
            SecurityContextHolder.clearContext();

            Map<String, String> response = new HashMap<>();
            response.put("message", "Logout successful");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Logout failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        try {
            System.out.println("🔍 Profile request received");
            
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("🔐 Authentication object: " + (authentication != null ? authentication.getClass().getSimpleName() : "null"));
            System.out.println("🔐 Is authenticated: " + (authentication != null ? authentication.isAuthenticated() : "N/A"));
            System.out.println("🔐 Principal: " + (authentication != null ? authentication.getPrincipal() : "N/A"));
            System.out.println("🔐 Name: " + (authentication != null ? authentication.getName() : "N/A"));
            
            if (authentication == null || !authentication.isAuthenticated()) {
                System.out.println("❌ Not authenticated");
                Map<String, String> response = new HashMap<>();
                response.put("error", "Not authenticated");
                return ResponseEntity.status(401).body(response);
            }

            String email = authentication.getName();
            System.out.println("📧 Looking up user with email: " + email);
            
            if (email == null || email.trim().isEmpty()) {
                System.out.println("❌ Email is null or empty");
                Map<String, String> response = new HashMap<>();
                response.put("error", "Invalid authentication: email not found");
                return ResponseEntity.status(401).body(response);
            }
            
            User user = userDAO.findByEmail(email);
            System.out.println("👤 User found: " + (user != null ? user.getEmail() + " (ID: " + user.getId() + ")" : "null"));
            
            if (user == null) {
                System.out.println("❌ User not found in database");
                Map<String, String> response = new HashMap<>();
                response.put("error", "User not found");
                return ResponseEntity.status(404).body(response);
            }

            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("email", user.getEmail());
            userData.put("firstName", user.getFirstName() != null ? user.getFirstName() : "");
            userData.put("lastName", user.getLastName() != null ? user.getLastName() : "");
            userData.put("createdAt", user.getCreatedAt() != null ? user.getCreatedAt() : "");

            Map<String, Object> response = new HashMap<>();
            response.put("user", userData);

            System.out.println("✅ Profile response sent successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("❌ Profile error: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to get profile: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody UserDTO userDTO, 
                                         HttpServletRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Not authenticated");
                return ResponseEntity.status(401).body(response);
            }

            String email = authentication.getName();
            User user = userDAO.findByEmail(email);

            // Update user fields
            if (userDTO.getFirstName() != null) {
                user.setFirstName(userDTO.getFirstName());
            }
            if (userDTO.getLastName() != null) {
                user.setLastName(userDTO.getLastName());
            }

            userDAO.updateUser(user);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Profile updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to update profile: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> body, HttpServletRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Not authenticated");
                return ResponseEntity.status(401).body(response);
            }
            String email = authentication.getName();
            User user = userDAO.findByEmail(email);
            String currentPassword = body.get("currentPassword");
            String newPassword = body.get("newPassword");
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Current password is incorrect");
                return ResponseEntity.badRequest().body(response);
            }
            if (newPassword == null || newPassword.length() < 6) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "New password must be at least 6 characters");
                return ResponseEntity.badRequest().body(response);
            }
            user.setPassword(passwordEncoder.encode(newPassword));
            userDAO.updateUser(user);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Password changed successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to change password: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
} 