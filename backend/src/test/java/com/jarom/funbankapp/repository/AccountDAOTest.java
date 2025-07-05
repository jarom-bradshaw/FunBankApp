package com.jarom.funbankapp.repository;

import com.jarom.funbankapp.model.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AccountDAOTest {

    private JdbcTemplate jdbcTemplate;
    private AccountDAO accountDAO;

    @BeforeEach
    void setUp() {
        // Create a mock JdbcTemplate instance
        jdbcTemplate = mock(JdbcTemplate.class);
        // Inject the mock into your DAO
        accountDAO = new AccountDAO(jdbcTemplate);
    }

    @Test
    void testCreateAccount() {
        // Arrange: Create a dummy Account
        Account account = new Account(null, 1L, "ACC123", new BigDecimal("100.00"), "Checking", null);
        // Simulate the update call returns 1 row affected
        when(jdbcTemplate.update(
                anyString(),
                eq(account.getUserId()),
                eq(account.getAccountNumber()),
                eq(account.getBalance()),
                eq(account.getAccountType())
        )).thenReturn(1);

        // Act: Call the method under test
        int result = accountDAO.createAccount(account);

        // Assert: Check that the result is as expected
        assertEquals(1, result);
        verify(jdbcTemplate, times(1)).update(
                eq("INSERT INTO accounts (user_id, account_number, balance, account_type) VALUES (?, ?, ?, ?)"),
                eq(account.getUserId()),
                eq(account.getAccountNumber()),
                eq(account.getBalance()),
                eq(account.getAccountType())
        );
    }

    @Test
    void testFindByUserId() {
        // Arrange: Set up the expected userId and a dummy account list
        Long userId = 1L;
        Account account = new Account(1L, userId, "ACC123", new BigDecimal("100.00"), "Checking", new Timestamp(System.currentTimeMillis()));
        List<Account> expectedAccounts = Arrays.asList(account);

        // Simulate the query call returning our list
        when(jdbcTemplate.query(
                anyString(),
                any(RowMapper.class),
                eq(userId)
        )).thenReturn(expectedAccounts);

        // Act: Call the method under test
        List<Account> result = accountDAO.findByUserId(userId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ACC123", result.get(0).getAccountNumber());
        verify(jdbcTemplate, times(1)).query(
                eq("SELECT * FROM accounts WHERE user_id = ?"),
                any(RowMapper.class),
                eq(userId)
        );
    }

    @Test
    void testGetBalance() {
        // Arrange: Set up an accountId and expected balance
        Long accountId = 1L;
        BigDecimal expectedBalance = new BigDecimal("250.50");

        // Simulate the queryForObject call
        when(jdbcTemplate.queryForObject(
                anyString(),
                eq(BigDecimal.class),
                eq(accountId)
        )).thenReturn(expectedBalance);

        // Act
        BigDecimal actualBalance = accountDAO.getBalance(accountId);

        // Assert
        assertEquals(expectedBalance, actualBalance);
        verify(jdbcTemplate, times(1)).queryForObject(
                eq("SELECT balance FROM accounts WHERE id = ?"),
                eq(BigDecimal.class),
                eq(accountId)
        );
    }

    @Test
    void testUpdateBalance() {
        // Arrange: Set up the accountId and the new balance
        Long accountId = 1L;
        BigDecimal newBalance = new BigDecimal("300.00");

        // Since jdbcTemplate.update returns an int, stub it to return 1
        when(jdbcTemplate.update(
                eq("UPDATE accounts SET balance = ? WHERE id = ?"),
                eq(newBalance),
                eq(accountId)
        )).thenReturn(1);

        // Act: Call the method that should update the balance
        accountDAO.updateBalance(accountId, newBalance);

        // Assert: Verify that jdbcTemplate.update was called with the correct query and parameters
        verify(jdbcTemplate, times(1)).update(
                eq("UPDATE accounts SET balance = ? WHERE id = ?"),
                eq(newBalance),
                eq(accountId)
        );
    }
}
