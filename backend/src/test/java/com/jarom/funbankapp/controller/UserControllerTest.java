// THIS TESTS ONLY BUSINESS LOGIC AND DISABLES THE SECURITY CONFIG ONLY FOR THIS FILE

package com.jarom.funbankapp.controller;

import com.jarom.funbankapp.model.LoginRequest;
import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.UserDAO;
import com.jarom.funbankapp.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.OverrideAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @MockBean
    private UserDAO userDAO;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // Set up default mocks for all tests
        when(userDAO.save(any(User.class))).thenReturn(1);
        when(passwordEncoder.encode(any(String.class))).thenReturn("hashedPassword");
        when(jwtService.generateToken(any(String.class))).thenReturn("dummyToken");
    }

    @Test
    void testRegister_UsernameExists() throws Exception {
        String requestJson = "{ \"username\": \"existingUser\", \"password\": \"password123\" }";
        when(userDAO.existsByUsername("existingUser")).thenReturn(true);
        when(userDAO.save(any(User.class))).thenReturn(0);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Username already exists."));
    }

    @Test
    void testRegister_Success() throws Exception {
        String requestJson = "{ \"username\": \"newUser\", \"password\": \"password123\" }";
        when(userDAO.existsByUsername("newUser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(userDAO.save(any(User.class))).thenReturn(1);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully!"));
    }

    @Test
    void testRegister_SaveReturnsZero() throws Exception {
        String requestJson = "{ \"username\": \"edgeUser\", \"password\": \"password123\" }";
        when(userDAO.existsByUsername("edgeUser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(userDAO.save(any(User.class))).thenReturn(0);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully!"));
    }

    @Test
    void testLogin_Success() throws Exception {
        String loginJson = "{ \"username\": \"testUser\", \"password\": \"password123\" }";
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("hashedPassword");

        when(userDAO.findByUsername("testUser")).thenReturn(user);
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);
        when(jwtService.generateToken("testUser")).thenReturn("dummyToken");

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Login successful"))
                .andExpect(header().string("Set-Cookie", containsString("auth-token=dummyToken")));
    }

    @Test
    void testLogin_InvalidCredentials() throws Exception {
        String loginJson = "{ \"username\": \"testUser\", \"password\": \"wrongPassword\" }";
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("hashedPassword");

        when(userDAO.findByUsername("testUser")).thenReturn(user);
        when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials"));
    }

    @Test
    void testLogin_UserNotFound() throws Exception {
        String loginJson = "{ \"username\": \"unknownUser\", \"password\": \"password123\" }";
        when(userDAO.findByUsername("unknownUser")).thenReturn(null);

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials"));
    }

    @Test
    void testLogin_Exception() throws Exception {
        String loginJson = "{ \"username\": \"unknownUser\", \"password\": \"password123\" }";
        when(userDAO.findByUsername("unknownUser")).thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials"));
    }

    @Test
    void testLogout_Success() throws Exception {
        mockMvc.perform(post("/api/users/logout"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Logout successful")))
                .andExpect(header().string("Set-Cookie", containsString("auth-token=")))
                .andExpect(header().string("Set-Cookie", containsString("Max-Age=0")));
    }

    @Test
    void testGetCsrfToken() throws Exception {
        mockMvc.perform(get("/api/users/csrf-token"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("CSRF token available in response headers")));
    }
}
