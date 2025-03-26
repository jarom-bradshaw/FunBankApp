package com.jarom.funbankapp.controller;

import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.UserDAO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder;

// Added hashing here
    public UserController(UserDAO userDAO, PasswordEncoder passwordEncoder) {
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userDAO.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body("Username already exists.");
        }

//        Hashing here
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        userDAO.save(user);
        return ResponseEntity.ok("User registered successfully!");
    }
}
