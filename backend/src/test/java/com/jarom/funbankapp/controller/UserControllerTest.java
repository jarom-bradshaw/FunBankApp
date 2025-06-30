// THIS TESTS ONLY BUSINESS LOGIC AND DISABLES THE SECURITY CONFIG ONLY FOR THIS FILE

package com.jarom.funbankapp.controller;

import com.jarom.funbankapp.model.LoginRequest;
import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.UserDAO;
import com.jarom.funbankapp.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.OverrideAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDAO userDAO;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtService jwtService;

    @Test
    void testRegister_UsernameExists() throws Exception {
        String requestJson = "{ \"username\": \"existingUser\", \"password\": \"password123\" }";
        when(userDAO.existsByUsername("existingUser")).thenReturn(true);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Username already exists.")));
    }

    @Test
    void testRegister_Success() throws Exception {
        String requestJson = "{ \"username\": \"newUser\", \"password\": \"password123\" }";
        when(userDAO.existsByUsername("newUser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("User registered successfully!")));

        verify(userDAO).save(argThat((User u) ->
                u.getUsername().equals("newUser") && u.getPassword().equals("hashedPassword")
        ));
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
                .andExpect(content().string(containsString("Bearer dummyToken")));
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
                .andExpect(content().string(containsString("Invalid credentials")));
    }

    @Test
    void testLogin_Exception() throws Exception {
        String loginJson = "{ \"username\": \"unknownUser\", \"password\": \"password123\" }";
        when(userDAO.findByUsername("unknownUser")).thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("Invalid credentials")));
    }
}
