package com.jarom.funbankapp.repository;

import java.util.Optional;

import com.jarom.funbankapp.model.User;

public interface UserRepository {
    
    // Create operations
    Long save(User user);
    
    // Read operations
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    
    // Update operations
    void updateUser(User user);
    
    // Delete operations
    void deleteUser(Long userId);
    
    // Validation operations
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
} 