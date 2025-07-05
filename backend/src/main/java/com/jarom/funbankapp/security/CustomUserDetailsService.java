package com.jarom.funbankapp.security;

import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.UserDAO;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserDAO userDAO;

    public CustomUserDetailsService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            User user = userDAO.findByEmail(email);
            if (user == null) {
                throw new UsernameNotFoundException("User not found with email: " + email);
            }

            return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("USER"))
            );
        } catch (Exception e) {
            throw new UsernameNotFoundException("Error loading user with email: " + email, e);
        }
    }
} 