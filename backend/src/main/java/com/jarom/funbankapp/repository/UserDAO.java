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

    public Long save(User user) {
        String sql = "INSERT INTO users (username, email, password_hash) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, user.getUsername(), user.getEmail(), user.getPassword());
        return jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
    }

    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username);
        return count != null && count > 0;
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    @SuppressWarnings("unused") // Will be used in /login
    public User findByUsername(String username) {
        String sql = "SELECT id, username, email, password_hash as password FROM users WHERE username = ?";
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), username);
    }

    public User findByEmail(String email) {
        String sql = "SELECT id, username, email, password_hash as password FROM users WHERE email = ?";
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), email);
    }

    public void updateUser(User user) {
        String sql = "UPDATE users SET email = ?, password_hash = ? WHERE username = ?";
        jdbcTemplate.update(sql, user.getEmail(), user.getPassword(), user.getUsername());
    }
}
