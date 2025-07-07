package com.jarom.funbankapp.model;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class ValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testUserValidation_ValidUser() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setFirstName("John");
        user.setLastName("Doe");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Valid user should have no violations");
    }

    @Test
    void testUserValidation_InvalidEmail() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("invalid-email");
        user.setPassword("password123");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Invalid email should have violations");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void testUserValidation_ShortPassword() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("123");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Short password should have violations");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }

    @Test
    void testAccountValidation_ValidAccount() {
        Account account = new Account();
        account.setUserId(1L);
        account.setAccountNumber("123456789");
        account.setBalance(new BigDecimal("1000.00"));
        account.setAccountType("checking");
        account.setName("My Checking Account");

        Set<ConstraintViolation<Account>> violations = validator.validate(account);
        assertTrue(violations.isEmpty(), "Valid account should have no violations");
    }

    @Test
    void testAccountValidation_InvalidAccountType() {
        Account account = new Account();
        account.setUserId(1L);
        account.setAccountNumber("123456789");
        account.setBalance(new BigDecimal("1000.00"));
        account.setAccountType("invalid-type");
        account.setName("My Account");

        Set<ConstraintViolation<Account>> violations = validator.validate(account);
        assertFalse(violations.isEmpty(), "Invalid account type should have violations");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("accountType")));
    }

    @Test
    void testTransactionValidation_ValidTransaction() {
        Transaction transaction = new Transaction();
        transaction.setAccountId(1L);
        transaction.setType("deposit");
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setDescription("Test deposit");

        Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);
        assertTrue(violations.isEmpty(), "Valid transaction should have no violations");
    }

    @Test
    void testTransactionValidation_InvalidAmount() {
        Transaction transaction = new Transaction();
        transaction.setAccountId(1L);
        transaction.setType("deposit");
        transaction.setAmount(new BigDecimal("-50.00"));
        transaction.setDescription("Test deposit");

        Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);
        assertFalse(violations.isEmpty(), "Negative amount should have violations");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("amount")));
    }
} 