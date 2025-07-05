package com.jarom.funbankapp.service;

import com.jarom.funbankapp.dto.UserDTO;
import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.UserDAO;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class UserService {

    private final UserDAO userDAO;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s'-]+$");

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public UserDTO sanitizeUserDTO(UserDTO userDTO) {
        if (userDTO.getEmail() != null) {
            userDTO.setEmail(userDTO.getEmail().trim().toLowerCase());
        }
        if (userDTO.getFirstName() != null) {
            userDTO.setFirstName(userDTO.getFirstName().trim());
        }
        if (userDTO.getLastName() != null) {
            userDTO.setLastName(userDTO.getLastName().trim());
        }
        return userDTO;
    }

    public boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public boolean isValidName(String name) {
        return name != null && NAME_PATTERN.matcher(name).matches() && name.length() <= 100;
    }

    public boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    public User convertToUser(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        return user;
    }

    public UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setCreatedAt(user.getCreatedAt() != null ? user.getCreatedAt().toLocalDateTime() : null);
        dto.setUpdatedAt(user.getUpdatedAt() != null ? user.getUpdatedAt().toLocalDateTime() : null);
        return dto;
    }
} 