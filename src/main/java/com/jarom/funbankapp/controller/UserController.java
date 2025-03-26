package com.jarom.funbankapp.controller;

import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;


@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserDAO userDAO;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userDAO.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body("Username already exists.");
        }

        userDAO.save(user);
        return ResponseEntity.ok("User registered successfully!");
    }
}
