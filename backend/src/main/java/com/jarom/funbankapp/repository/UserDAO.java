package com.jarom.funbankapp.repository;

import com.jarom.funbankapp.model.User;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserDAO {

    private final JdbcTemplate jdbcTemplate;

    public UserDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int save(User user) {
        String sql = "INSERT INTO users (username, email, password_hash) VALUES (?, ?, ?)";
        return jdbcTemplate.update(sql, user.getUsername(), user.getEmail(), user.getPassword());
    }

    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username);
        return count != null && count > 0;
    }

    @SuppressWarnings("unused") // Will be used in /login
    public User findByUsername(String username) {
        String sql = "SELECT id, username, email, password_hash as password FROM users WHERE username = ?";
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), username);
    }

    public void updateUser(User user) {
        String sql = "UPDATE users SET email = ?, password_hash = ? WHERE username = ?";
        jdbcTemplate.update(sql, user.getEmail(), user.getPassword(), user.getUsername());
    }
}
