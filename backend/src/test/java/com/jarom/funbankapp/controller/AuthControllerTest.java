package com.jarom.funbankapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jarom.funbankapp.dto.LoginRequest;
import com.jarom.funbankapp.dto.UserDTO;
import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.UserDAO;
import com.jarom.funbankapp.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.TestPropertySource;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@TestPropertySource(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
})
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private UserDAO userDAO;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private Authentication authentication;

    @Test
    void testRegister_Success() throws Exception {
        // Arrange
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@example.com");
        userDTO.setPassword("password123");
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");

        when(userDAO.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully"));
    }

    @Test
    void testRegister_UserExists() throws Exception {
        // Arrange
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("existing@example.com");
        userDTO.setPassword("password123");

        when(userDAO.existsByEmail("existing@example.com")).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("User with this email already exists"));
    }

    @Test
    void testLogin_Success() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setCreatedAt(LocalDateTime.of(2024, 1, 1, 12, 0, 0));

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(userDAO.findByEmail("test@example.com")).thenReturn(user);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.user.id").value(1))
                .andExpect(jsonPath("$.user.email").value("test@example.com"));
    }

    @Test
    void testLogin_InvalidCredentials() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid credentials"));
    }

    @Test
    void testLogout_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logout successful"));
    }

    @Test
    void testGetProfile_Success() throws Exception {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setCreatedAt(LocalDateTime.of(2024, 1, 1, 12, 0, 0));

        when(authentication.getName()).thenReturn("test@example.com");
        when(authentication.isAuthenticated()).thenReturn(true);
        when(userDAO.findByEmail("test@example.com")).thenReturn(user);

        // Set up security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act & Assert
        mockMvc.perform(get("/api/auth/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.id").value(1))
                .andExpect(jsonPath("$.user.email").value("test@example.com"));
    }

    @Test
    void testGetProfile_NotAuthenticated() throws Exception {
        // Arrange
        when(authentication.isAuthenticated()).thenReturn(false);

        // Act & Assert
        mockMvc.perform(get("/api/auth/profile"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Not authenticated"));
    }

    @Test
    void testUpdateProfile_Success() throws Exception {
        // Arrange
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("Jane");
        userDTO.setLastName("Smith");

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");

        when(authentication.getName()).thenReturn("test@example.com");
        when(authentication.isAuthenticated()).thenReturn(true);
        when(userDAO.findByEmail("test@example.com")).thenReturn(user);

        // Set up security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act & Assert
        mockMvc.perform(put("/api/auth/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Profile updated successfully"));
    }

    @Test
    void testChangePassword_Success() throws Exception {
        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("currentPassword", "oldpassword");
        request.put("newPassword", "newpassword");

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("hashedOldPassword");

        when(authentication.getName()).thenReturn("test@example.com");
        when(authentication.isAuthenticated()).thenReturn(true);
        when(userDAO.findByEmail("test@example.com")).thenReturn(user);
        when(passwordEncoder.matches("oldpassword", "hashedOldPassword")).thenReturn(true);
        when(passwordEncoder.encode("newpassword")).thenReturn("hashedNewPassword");

        // Set up security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act & Assert
        mockMvc.perform(put("/api/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password changed successfully"));
    }

    @Test
    void testChangePassword_InvalidCurrentPassword() throws Exception {
        // Arrange
        Map<String, String> body = new HashMap<>();
        body.put("currentPassword", "wrongpassword");
        body.put("newPassword", "newpassword123");

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("hashedPassword");

        when(authentication.getName()).thenReturn("test@example.com");
        when(authentication.isAuthenticated()).thenReturn(true);
        when(userDAO.findByEmail("test@example.com")).thenReturn(user);
        when(passwordEncoder.matches("wrongpassword", "hashedPassword")).thenReturn(false);

        // Set up security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act & Assert
        mockMvc.perform(put("/api/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Current password is incorrect"));
    }

    @Test
    void testLoginToProfileFlow_Success() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setCreatedAt(LocalDateTime.of(2024, 1, 1, 12, 0, 0));

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userDAO.findByEmail("test@example.com")).thenReturn(user);

        // Act & Assert - Login
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.user.id").value(1))
                .andExpect(jsonPath("$.user.email").value("test@example.com"));

        // Set up security context for profile request
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act & Assert - Profile
        mockMvc.perform(get("/api/auth/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.id").value(1))
                .andExpect(jsonPath("$.user.email").value("test@example.com"))
                .andExpect(jsonPath("$.user.firstName").value("John"))
                .andExpect(jsonPath("$.user.lastName").value("Doe"));
    }
} 