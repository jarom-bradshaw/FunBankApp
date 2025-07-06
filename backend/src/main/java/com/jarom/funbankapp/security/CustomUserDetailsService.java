package com.jarom.funbankapp.security;

import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        // First try to find by username
        Optional<User> userOpt = userRepository.findByUsername(usernameOrEmail);
        
        // If not found by username, try by email
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByEmail(usernameOrEmail);
        }
        
        User user = userOpt.orElseThrow(() -> 
            new UsernameNotFoundException("User not found: " + usernameOrEmail));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), // Use username as the principal name
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("USER"))
        );
    }
} 