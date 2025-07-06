package com.jarom.funbankapp.service;

import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Long save(User user) {
        return userRepository.save(user);
    }

    public java.util.Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public java.util.Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void updateUser(User user) {
        userRepository.updateUser(user);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
} 