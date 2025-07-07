package com.jarom.funbankapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jarom.funbankapp.model.LoginRequest;
import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.UserRepository;
import com.jarom.funbankapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserService userService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private User user;
    private User existingUser;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setFirstName("John");
        user.setLastName("Doe");

        existingUser = new User();
        existingUser.setId(2L);
        existingUser.setUsername("existinguser");
        existingUser.setEmail("existing@example.com");
        existingUser.setPassword("encodedPassword");
    }

    @Test
    void register_WithNewEmail_ShouldReturnSuccess() throws Exception {
        // Arrange
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user.getId());

        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setEmail("new@example.com");
        newUser.setPassword("password123");
        newUser.setFirstName("Jane");
        newUser.setLastName("Smith");

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User registered successfully"));
    }

    @Test
    void register_WithExistingEmail_ShouldReturnError() throws Exception {
        // Arrange
        when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(existingUser));

        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setEmail("existing@example.com");
        newUser.setPassword("password123");

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Email already exists"));
    }

    @Test
    void login_WithValidCredentials_ShouldReturnSuccess() throws Exception {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("test@example.com");
        loginRequest.setPassword("password123");

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login successful"));
    }

    @Test
    void login_WithInvalidEmail_ShouldReturnError() throws Exception {
        // Arrange
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("nonexistent@example.com");
        loginRequest.setPassword("password123");

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }

    @Test
    void login_WithInvalidPassword_ShouldReturnError() throws Exception {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("test@example.com");
        loginRequest.setPassword("wrongpassword");

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }

    @Test
    void logout_ShouldReturnSuccess() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Logout successful"));
    }

    @Test
    void validateToken_WithValidToken_ShouldReturnSuccess() throws Exception {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // Act & Assert
        mockMvc.perform(post("/auth/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\": \"valid-token\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Token is valid"));
    }

    @Test
    void validateToken_WithInvalidToken_ShouldReturnError() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/auth/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\": \"invalid-token\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid token"));
    }
} 
