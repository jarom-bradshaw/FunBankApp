package com.jarom.funbankapp.repository;

import com.jarom.funbankapp.model.Transaction;
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

public class TransactionDAOTest {

    private JdbcTemplate jdbcTemplate;
    private TransactionDAO transactionDAO;

    @BeforeEach
    void setUp() {
        jdbcTemplate = mock(JdbcTemplate.class);
        transactionDAO = new TransactionDAO(jdbcTemplate);
    }

    @Test
    void testLogTransaction() {
        // Arrange: Define transaction details
        int accountId = 1;
        String type = "deposit";
        BigDecimal amount = new BigDecimal("150.00");
        String description = "Deposit for savings";

        // Expect jdbcTemplate.update to be called with the given SQL and parameters, returning 1 row updated.
        when(jdbcTemplate.update(
                eq("INSERT INTO transactions (account_id, type, amount, description) VALUES (?, ?, ?, ?)"),
                eq(accountId),
                eq(type),
                eq(amount),
                eq(description)
        )).thenReturn(1);

        // Act: Call the DAO method
        int result = transactionDAO.logTransaction(accountId, type, amount, description);

        // Assert: Verify that the method returns 1 and the update was called correctly.
        assertEquals(1, result);
        verify(jdbcTemplate, times(1)).update(
                eq("INSERT INTO transactions (account_id, type, amount, description) VALUES (?, ?, ?, ?)"),
                eq(accountId),
                eq(type),
                eq(amount),
                eq(description)
        );
    }

    @Test
    void testFindByAccountId() {
        // Arrange: Set up an accountId and a dummy transaction list.
        int accountId = 1;
        Transaction transaction = new Transaction(
                1,                              // id
                accountId,                      // account_id
                "withdrawal",                   // type
                new BigDecimal("50.00"),         // amount
                "ATM Withdrawal",               // description
                new Timestamp(System.currentTimeMillis()) // created_at
        );
        List<Transaction> expectedTransactions = Arrays.asList(transaction);

        // Simulate the jdbcTemplate query returning our list of transactions.
        when(jdbcTemplate.query(
                eq("SELECT * FROM transactions WHERE account_id = ? ORDER BY created_at DESC"),
                any(RowMapper.class),
                eq(accountId)
        )).thenReturn(expectedTransactions);

        // Act: Call the DAO method
        List<Transaction> result = transactionDAO.findByAccountId(accountId);

        // Assert: Validate the result and verify the correct SQL was used.
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ATM Withdrawal", result.get(0).getDescription());
        verify(jdbcTemplate, times(1)).query(
                eq("SELECT * FROM transactions WHERE account_id = ? ORDER BY created_at DESC"),
                any(RowMapper.class),
                eq(accountId)
        );
    }
}
