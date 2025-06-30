package com.jarom.funbankapp.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jarom.funbankapp.dto.DepositRequest;
import com.jarom.funbankapp.dto.TransferRequest;
import com.jarom.funbankapp.dto.WithdrawRequest;
import com.jarom.funbankapp.model.Account;
import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.AccountDAO;
import com.jarom.funbankapp.repository.TransactionDAO;
import com.jarom.funbankapp.repository.UserDAO;
import com.jarom.funbankapp.security.CookieAuthFilter;
import com.jarom.funbankapp.service.FinancialAnalysisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@WebMvcTest(
        controllers = AccountController.class,
        excludeFilters = @Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {CookieAuthFilter.class}
        ),
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class}
)
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountDAO accountDAO;

    @MockBean
    private UserDAO userDAO;

    @MockBean
    private TransactionDAO transactionDAO;

    @MockBean
    private FinancialAnalysisService financialAnalysisService;

    @BeforeEach
    void setUp() {
        // Set up security context for all tests
        Authentication auth = new UsernamePasswordAuthenticationToken("testuser", null);
        SecurityContext securityContext = org.mockito.Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
    }

    // Helper method to create a dummy user
    private User createDummyUser(String username, int id) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        return user;
    }

    // Helper method to create a dummy account
    private Account createDummyAccount(int id, int userId, BigDecimal balance) {
        Account account = new Account();
        account.setId(id);
        account.setUserId(userId);
        account.setBalance(balance);
        return account;
    }

    @Test
    public void testGetUserAccounts() throws Exception {
        // Arrange
        User dummyUser = createDummyUser("testuser", 1);
        when(userDAO.findByUsername("testuser")).thenReturn(dummyUser);

        Account account = createDummyAccount(10, 1, BigDecimal.valueOf(100));
        List<Account> accounts = Collections.singletonList(account);
        when(accountDAO.findByUserId(1)).thenReturn(accounts);

        // Act & Assert
        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(accounts)));
    }

    @Test
    public void testGetUserAccounts_NoAccounts() throws Exception {
        // Arrange
        User dummyUser = createDummyUser("testuser", 1);
        when(userDAO.findByUsername("testuser")).thenReturn(dummyUser);
        when(accountDAO.findByUserId(1)).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    public void testCreateAccount() throws Exception {
        // Arrange
        User dummyUser = createDummyUser("testuser", 1);
        when(userDAO.findByUsername("testuser")).thenReturn(dummyUser);
        when(accountDAO.createAccount(any(Account.class))).thenReturn(1);

        Account requestAccount = new Account();
        requestAccount.setBalance(BigDecimal.valueOf(100));

        // Act & Assert
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestAccount)))
                .andExpect(status().isOk())
                .andExpect(content().string("Account created."));

        verify(accountDAO).createAccount(any(Account.class));
    }

    @Test
    public void testDeposit_Success() throws Exception {
        // Arrange
        User dummyUser = createDummyUser("testuser", 1);
        when(userDAO.findByUsername("testuser")).thenReturn(dummyUser);

        Account account = createDummyAccount(10, 1, BigDecimal.valueOf(200));
        when(accountDAO.findByUserId(1)).thenReturn(Collections.singletonList(account));
        when(accountDAO.getBalance(10)).thenReturn(BigDecimal.valueOf(200));
        doNothing().when(accountDAO).updateBalance(eq(10), any(BigDecimal.class));
        when(transactionDAO.logTransaction(eq(10), eq("deposit"), any(BigDecimal.class), anyString())).thenReturn(1);

        DepositRequest depositRequest = new DepositRequest();
        depositRequest.setAccountId(10);
        depositRequest.setAmount(BigDecimal.valueOf(100));
        depositRequest.setDescription("Test Deposit");

        // Act & Assert
        mockMvc.perform(post("/api/accounts/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Deposit successful")));
    }

    @Test
    public void testDeposit_Unauthorized() throws Exception {
        // Arrange
        User dummyUser = createDummyUser("testuser", 1);
        when(userDAO.findByUsername("testuser")).thenReturn(dummyUser);
        when(accountDAO.findByUserId(1)).thenReturn(Collections.emptyList());

        DepositRequest depositRequest = new DepositRequest();
        depositRequest.setAccountId(20);
        depositRequest.setAmount(BigDecimal.valueOf(50));
        depositRequest.setDescription("Deposit");

        // Act & Assert
        mockMvc.perform(post("/api/accounts/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Unauthorized: You don't own this account."));
    }

    @Test
    public void testDeposit_NullDescription() throws Exception {
        // Arrange
        User dummyUser = createDummyUser("testuser", 1);
        when(userDAO.findByUsername("testuser")).thenReturn(dummyUser);
        Account account = createDummyAccount(10, 1, BigDecimal.valueOf(200));
        when(accountDAO.findByUserId(1)).thenReturn(Collections.singletonList(account));
        when(accountDAO.getBalance(10)).thenReturn(BigDecimal.valueOf(200));
        doNothing().when(accountDAO).updateBalance(eq(10), any(BigDecimal.class));
        when(transactionDAO.logTransaction(eq(10), eq("deposit"), any(BigDecimal.class), any())).thenReturn(1);

        DepositRequest depositRequest = new DepositRequest();
        depositRequest.setAccountId(10);
        depositRequest.setAmount(BigDecimal.valueOf(100));
        depositRequest.setDescription(null);

        // Act & Assert
        mockMvc.perform(post("/api/accounts/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Deposit successful")));
    }

    @Test
    public void testWithdraw_Success() throws Exception {
        // Arrange
        User dummyUser = createDummyUser("testuser", 1);
        when(userDAO.findByUsername("testuser")).thenReturn(dummyUser);

        Account account = createDummyAccount(10, 1, BigDecimal.valueOf(200));
        when(accountDAO.findByUserId(1)).thenReturn(Collections.singletonList(account));
        when(accountDAO.getBalance(10)).thenReturn(BigDecimal.valueOf(200));
        doNothing().when(accountDAO).updateBalance(eq(10), any(BigDecimal.class));
        when(transactionDAO.logTransaction(eq(10), eq("withdraw"), any(BigDecimal.class), anyString())).thenReturn(1);

        WithdrawRequest withdrawRequest = new WithdrawRequest();
        withdrawRequest.setAccountId(10);
        withdrawRequest.setAmount(BigDecimal.valueOf(50));
        withdrawRequest.setDescription("Withdrawal");

        // Act & Assert
        mockMvc.perform(post("/api/accounts/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Withdrawal successful")));
    }

    @Test
    public void testWithdraw_InsufficientFunds() throws Exception {
        // Arrange
        User dummyUser = createDummyUser("testuser", 1);
        when(userDAO.findByUsername("testuser")).thenReturn(dummyUser);

        Account account = createDummyAccount(10, 1, BigDecimal.valueOf(50));
        when(accountDAO.findByUserId(1)).thenReturn(Collections.singletonList(account));
        when(accountDAO.getBalance(10)).thenReturn(BigDecimal.valueOf(50));

        WithdrawRequest withdrawRequest = new WithdrawRequest();
        withdrawRequest.setAccountId(10);
        withdrawRequest.setAmount(BigDecimal.valueOf(100));

        // Act & Assert
        mockMvc.perform(post("/api/accounts/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Insufficient funds."));
    }

    @Test
    public void testWithdraw_Unauthorized() throws Exception {
        // Arrange
        User dummyUser = createDummyUser("testuser", 1);
        when(userDAO.findByUsername("testuser")).thenReturn(dummyUser);
        when(accountDAO.findByUserId(1)).thenReturn(Collections.emptyList());

        WithdrawRequest withdrawRequest = new WithdrawRequest();
        withdrawRequest.setAccountId(20);
        withdrawRequest.setAmount(BigDecimal.valueOf(50));

        // Act & Assert
        mockMvc.perform(post("/api/accounts/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawRequest)))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Unauthorized: You don't own this account."));
    }

    @Test
    public void testTransfer_Success() throws Exception {
        // Arrange
        User dummyUser = createDummyUser("testuser", 1);
        when(userDAO.findByUsername("testuser")).thenReturn(dummyUser);

        Account sourceAccount = createDummyAccount(10, 1, BigDecimal.valueOf(300));
        when(accountDAO.findByUserId(1)).thenReturn(Collections.singletonList(sourceAccount));
        when(accountDAO.getBalance(10)).thenReturn(BigDecimal.valueOf(300));
        when(accountDAO.getBalance(20)).thenReturn(BigDecimal.valueOf(100));
        doNothing().when(accountDAO).updateBalance(eq(10), any(BigDecimal.class));
        doNothing().when(accountDAO).updateBalance(eq(20), any(BigDecimal.class));
        when(transactionDAO.logTransaction(eq(10), eq("transfer"), any(BigDecimal.class), anyString())).thenReturn(1);
        when(transactionDAO.logTransaction(eq(20), eq("deposit"), any(BigDecimal.class), anyString())).thenReturn(1);

        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setFromAccountId(10);
        transferRequest.setToAccountId(20);
        transferRequest.setAmount(BigDecimal.valueOf(50));
        transferRequest.setDescription("Transfer to account 20");

        // Act & Assert
        mockMvc.perform(post("/api/accounts/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Transfer successful."));
    }

    @Test
    public void testTransfer_InsufficientFunds() throws Exception {
        // Arrange
        User dummyUser = createDummyUser("testuser", 1);
        when(userDAO.findByUsername("testuser")).thenReturn(dummyUser);

        Account sourceAccount = createDummyAccount(10, 1, BigDecimal.valueOf(30));
        when(accountDAO.findByUserId(1)).thenReturn(Collections.singletonList(sourceAccount));
        when(accountDAO.getBalance(10)).thenReturn(BigDecimal.valueOf(30));

        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setFromAccountId(10);
        transferRequest.setToAccountId(20);
        transferRequest.setAmount(BigDecimal.valueOf(50));

        // Act & Assert
        mockMvc.perform(post("/api/accounts/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Insufficient funds."));
    }

    @Test
    public void testTransfer_UnauthorizedSourceAccount() throws Exception {
        // Arrange
        User dummyUser = createDummyUser("testuser", 1);
        when(userDAO.findByUsername("testuser")).thenReturn(dummyUser);
        when(accountDAO.findByUserId(1)).thenReturn(Collections.emptyList());

        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setFromAccountId(10);
        transferRequest.setToAccountId(20);
        transferRequest.setAmount(BigDecimal.valueOf(50));

        // Act & Assert
        mockMvc.perform(post("/api/accounts/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Unauthorized: You don't own the source account."));
    }

    @Test
    public void testTransfer_NullDescription() throws Exception {
        // Arrange
        User dummyUser = createDummyUser("testuser", 1);
        when(userDAO.findByUsername("testuser")).thenReturn(dummyUser);
        Account sourceAccount = createDummyAccount(10, 1, BigDecimal.valueOf(300));
        when(accountDAO.findByUserId(1)).thenReturn(Collections.singletonList(sourceAccount));
        when(accountDAO.getBalance(10)).thenReturn(BigDecimal.valueOf(300));
        when(accountDAO.getBalance(20)).thenReturn(BigDecimal.valueOf(100));
        doNothing().when(accountDAO).updateBalance(eq(10), any(BigDecimal.class));
        doNothing().when(accountDAO).updateBalance(eq(20), any(BigDecimal.class));
        when(transactionDAO.logTransaction(eq(10), eq("transfer"), any(BigDecimal.class), any())).thenReturn(1);
        when(transactionDAO.logTransaction(eq(20), eq("deposit"), any(BigDecimal.class), any())).thenReturn(1);

        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setFromAccountId(10);
        transferRequest.setToAccountId(20);
        transferRequest.setAmount(BigDecimal.valueOf(50));
        transferRequest.setDescription(null);

        // Act & Assert
        mockMvc.perform(post("/api/accounts/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Transfer successful."));
    }

    @Test
    public void testAnalyzeFinancialData() throws Exception {
        // Arrange
        User dummyUser = createDummyUser("testuser", 1);
        when(userDAO.findByUsername("testuser")).thenReturn(dummyUser);
        String input = "\"test input\"";
        String expectedAnalysis = "Analysis result";
        when(financialAnalysisService.analyzeWithOllama("test input")).thenReturn(expectedAnalysis);

        // Act & Assert
        mockMvc.perform(post("/api/accounts/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(input))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedAnalysis));
    }

    @Test
    public void testAnalyzeFinancialData_EmptyInput() throws Exception {
        // Arrange
        User dummyUser = createDummyUser("testuser", 1);
        when(userDAO.findByUsername("testuser")).thenReturn(dummyUser);
        String input = "\"\"";
        when(financialAnalysisService.analyzeWithOllama("\"")).thenReturn("");

        // Act & Assert
        mockMvc.perform(post("/api/accounts/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(input))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    public void testAnalyzeFinancialData_ServiceException() throws Exception {
        // Arrange
        User dummyUser = createDummyUser("testuser", 1);
        when(userDAO.findByUsername("testuser")).thenReturn(dummyUser);
        String input = "\"test input\"";
        when(financialAnalysisService.analyzeWithOllama("test input")).thenThrow(new RuntimeException("Service error"));

        // Act & Assert
        mockMvc.perform(post("/api/accounts/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(input))
                .andExpect(status().isInternalServerError());
    }
}
