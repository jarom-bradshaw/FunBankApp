package com.jarom.funbankapp.controller;

import com.jarom.funbankapp.model.Account;
import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.AccountDAO;
import com.jarom.funbankapp.repository.UserDAO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountDAO accountDAO;
    private final UserDAO userDAO;

    public AccountController(AccountDAO accountDAO, UserDAO userDAO) {
        this.accountDAO = accountDAO;
        this.userDAO = userDAO;
    }

    // Create a new account
    @PostMapping
    public ResponseEntity<?> createAccount(@RequestBody Account request) {
        String username = getCurrentUsername();
        User user = userDAO.findByUsername(username);

        request.setUserId(user.getId());
        accountDAO.createAccount(request);

        return ResponseEntity.ok("Account created.");
    }

    // Get all accounts for the logged-in user
    @GetMapping
    public ResponseEntity<List<Account>> getUserAccounts() {
        String username = getCurrentUsername();
        User user = userDAO.findByUsername(username);

        List<Account> accounts = accountDAO.findByUserId(user.getId());
        return ResponseEntity.ok(accounts);
    }

    // Helper method to get username from JWT
    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }
}
