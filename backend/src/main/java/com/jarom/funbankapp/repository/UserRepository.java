package com.jarom.funbankapp.repository;

import com.jarom.funbankapp.model.User;
import java.util.Optional;

public interface UserRepository {
    
    // Create operations
    Long save(User user);
    
    // Read operations
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    
    // Existence checks
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    
    // Update operations
    void updateUser(User user);
} 