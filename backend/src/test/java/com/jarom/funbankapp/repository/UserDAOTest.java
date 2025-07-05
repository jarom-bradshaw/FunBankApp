package com.jarom.funbankapp.repository;

import com.jarom.funbankapp.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserDAOTest {

    private JdbcTemplate jdbcTemplate;
    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        jdbcTemplate = mock(JdbcTemplate.class);
        userDAO = new UserDAO(jdbcTemplate);
    }

    @Test
    void testExistsByUsernameReturnsTrue() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq("testuser"))).thenReturn(1);

        boolean exists = userDAO.existsByUsername("testuser");

        assertTrue(exists);
        verify(jdbcTemplate, times(1)).queryForObject(anyString(), eq(Integer.class), eq("testuser"));
    }

    @Test
    void testExistsByUsernameReturnsFalse() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq("nouser"))).thenReturn(0);

        boolean exists = userDAO.existsByUsername("nouser");

        assertFalse(exists);
    }

    @Test
    void testSaveUserReturnsOne() {
        User user = new User();
        user.setUsername("jarom");
        user.setEmail("jarom@example.com");
        user.setPassword("hashed");

        when(jdbcTemplate.update(anyString(), anyString(), anyString(), anyString())).thenReturn(1);

        Long result = userDAO.save(user);

        assertEquals(1, result);
    }

    @Test
    void testFindByUsernameReturnsUser() {
        User mockUser = new User();
        mockUser.setUsername("jarom");
        mockUser.setPassword("hashed");

        when(jdbcTemplate.queryForObject(
                anyString(),
                any(BeanPropertyRowMapper.class),
                eq("jarom"))
        ).thenReturn(mockUser);

        User user = userDAO.findByUsername("jarom");

        assertNotNull(user);
        assertEquals("jarom", user.getUsername());
    }
}
