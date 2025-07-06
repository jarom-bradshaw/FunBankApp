package com.jarom.funbankapp.controller;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jarom.funbankapp.dto.ApiResponse;
import com.jarom.funbankapp.dto.LoginRequest;
import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.UserRepository;
import com.jarom.funbankapp.security.JwtService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, 
                         JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account with the provided credentials")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "User registered successfully",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Username or email already exists, or invalid data provided")
    })
    public ResponseEntity<ApiResponse<Map<String, String>>> register(
        @Parameter(description = "User registration data", required = true)
        @Valid @RequestBody User request
    ) {
        try {
            // Check if username already exists
            if (userRepository.findByUsername(request.getUsername()).isPresent()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Username already exists"));
            }

            // Check if email already exists
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Email already exists"));
            }

            // Create new user
            User user = new User();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

            Long userId = userRepository.save(user);
            user.setId(userId);

            // Generate JWT token
            String token = jwtService.generateToken(user.getUsername());

            Map<String, String> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("token", token);

            return ResponseEntity.ok(ApiResponse.success("User registered successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Registration failed: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates user and returns JWT token")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Login successful",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))       
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid credentials")
    })
    public ResponseEntity<ApiResponse<Map<String, String>>> login(
        @Parameter(description = "Login credentials", required = true)
        @Valid @RequestBody LoginRequest request
    ) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            String token = jwtService.generateToken(authentication.getName());

            Map<String, String> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("token", token);

            return ResponseEntity.ok(ApiResponse.success("Login successful", response));   
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Login failed: " + e.getMessage()));
        }
    }

    @GetMapping("/validate")
    @Operation(summary = "Validate JWT token", description = "Validates the current JWT token and returns user information")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Token is valid",
            content = @Content(schema = @Schema(implementation = com.jarom.funbankapp.dto.ApiResponse.class))       
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token is invalid or missing")    
    })
    public ResponseEntity<ApiResponse<String>> validateToken(Authentication authentication) {
        try {
            if (authentication != null && authentication.isAuthenticated()) {
                return ResponseEntity.ok(ApiResponse.success("Token is valid", "Valid"));  
            } else {
                return ResponseEntity.status(401).body(ApiResponse.error("Token is invalid"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(401).body(ApiResponse.error("Token validation failed: " + e.getMessage()));
        }
    }
} 