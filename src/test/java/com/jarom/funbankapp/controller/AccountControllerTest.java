package com.jarom.funbankapp.controller;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jarom.funbankapp.dto.AccountDTO;
import com.jarom.funbankapp.dto.DepositRequest;
import com.jarom.funbankapp.dto.TransferRequest;
import com.jarom.funbankapp.dto.WithdrawRequest;
import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.AccountRepository;
import com.jarom.funbankapp.repository.TransactionRepository;
import com.jarom.funbankapp.repository.UserRepository;
import com.jarom.funbankapp.security.JwtAuthFilter;
import com.jarom.funbankapp.service.AccountService;

@WebMvcTest(
        controllers = AccountController.class,
        excludeFilters = @Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthFilter.class}
        ),
        // Exclude security auto-configurations since we are focusing on business logic.
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class}
)
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountService accountService;

    // Remove these mocks for a pure controller test
    // @MockBean
    // private UserRepository userRepository;
    // @MockBean
    // private AccountRepository accountRepository;
    // @MockBean
    // private TransactionRepository transactionRepository;

    // Helper method to set up a dummy user.
    private User createDummyUser(String username, Long id) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        return user;
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testGetUserAccounts() throws Exception {
        // Arrange
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setId(1L);
        accountDTO.setName("Test Account");
        accountDTO.setAccountNumber("1234567890");
        accountDTO.setBalance(BigDecimal.valueOf(100.00));
        accountDTO.setAccountType(AccountDTO.AccountType.CHECKING);
        accountDTO.setColor("#FF0000");
        
        List<AccountDTO> accounts = Collections.singletonList(accountDTO);
        when(accountService.getUserAccounts()).thenReturn(accounts);
        // Act & Assert
        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk());
    }

//    @Test
//    @WithMockUser(username = "testuser")
//    public void testCreateAccount() throws Exception {
//        // Arrange
//        User dummyUser = createDummyUser("testuser", 1);
//        when(userDAO.findByUsername("testuser")).thenReturn(dummyUser);
//
//        // Create a new account request (the controller sets the account number and user id)
//        Account requestAccount = new Account();
//        requestAccount.setBalance(BigDecimal.valueOf(100));
//
//        // Assume accountDAO.createAccount() is a void method (or its return value is unused)
//        doNothing().when(accountDAO).createAccount(any(Account.class));
//
//        // Act & Assert
//        mockMvc.perform(post("/api/accounts")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(requestAccount)))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Account created."));
//    }

//    @Test
//    @WithMockUser(username = "testuser")
//    public void testDeposit_Success() throws Exception {
//        // Arrange: set up test data for deposit
//        DepositRequest depositRequest = new DepositRequest();
//        depositRequest.setAccountId(1);
//        depositRequest.setAmount(new BigDecimal("100.00"));
//        depositRequest.setDescription("Test Deposit");
//
//        // Assume user ownership is already confirmed by your mocks.
//        // Stub the DAO calls that are not under test:
//
//        // For example, stub accountDAO.getBalance(accountId) to return some current balance
//        when(accountDAO.getBalance(1)).thenReturn(new BigDecimal("200.00"));
//
//        // Stub the accountDAO.updateBalance method if needed (if it returns void, use doNothing())
//        doNothing().when(accountDAO).updateBalance(eq(1), any(BigDecimal.class));
//
//        // **Important**: Stub transactionDAO.logTransaction call properly since it returns int.
//        when(transactionDAO.logTransaction(
//                eq(1),
//                eq("deposit"),
//                eq(new BigDecimal("100.00")),
//                eq("Test Deposit")
//        )).thenReturn(1);
//
//        // Act: simulate the deposit request using MockMvc or direct controller invocation.
//        // For example, if you are using MockMvc:
//        mockMvc.perform(post("/api/accounts/deposit")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(asJsonString(depositRequest)))
//                .andExpect(status().isOk())
//                .andExpect(content().string(containsString("Deposit successful")));
//    }

    // Utility method to convert an object to JSON (if needed)
    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    @WithMockUser(username = "testuser")
    public void testDeposit_Unauthorized() throws Exception {
        // Arrange
        DepositRequest depositRequest = new DepositRequest();
        depositRequest.setAccountId(20L);
        depositRequest.setAmount(BigDecimal.valueOf(50));
        depositRequest.setDescription("Deposit");
        doThrow(new RuntimeException("Unauthorized: You don't own this account.")).when(accountService).deposit(any(DepositRequest.class));
        // Act & Assert: Expect a 403 Forbidden response.
        mockMvc.perform(post("/api/accounts/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isForbidden())
                .andExpect(content().json("{\"error\":\"Forbidden\",\"message\":\"Unauthorized: You don't own this account.\",\"status\":403}"));
    }

//    @Test
//    @WithMockUser(username = "testuser")
//    public void testWithdraw_Success() throws Exception {
//        // Arrange
//        User dummyUser = createDummyUser("testuser", 1);
//        when(userDAO.findByUsername("testuser")).thenReturn(dummyUser);
//
//        Account account = new Account();
//        account.setId(10);
//        account.setUserId(1);
//        when(accountDAO.findByUserId(1)).thenReturn(Collections.singletonList(account));
//        when(accountDAO.getBalance(10)).thenReturn(BigDecimal.valueOf(200));
//
//        doNothing().when(accountDAO).updateBalance(10, BigDecimal.valueOf(150));
//        doNothing().when(transactionDAO).logTransaction(10, "withdraw", BigDecimal.valueOf(50), "Withdrawal");
//
//        WithdrawRequest withdrawRequest = new WithdrawRequest();
//        withdrawRequest.setAccountId(10);
//        withdrawRequest.setAmount(BigDecimal.valueOf(50));
//        withdrawRequest.setDescription("Withdrawal");
//
//        // Act & Assert
//        mockMvc.perform(post("/api/accounts/withdraw")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(withdrawRequest)))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Withdrawal successful. New balance: $150"));
//    }

    @Test
    @WithMockUser(username = "testuser")
    public void testWithdraw_InsufficientFunds() throws Exception {
        // Arrange
        WithdrawRequest withdrawRequest = new WithdrawRequest();
        withdrawRequest.setAccountId(10L);
        withdrawRequest.setAmount(BigDecimal.valueOf(100));
        doThrow(new RuntimeException("Insufficient funds.")).when(accountService).withdraw(any(WithdrawRequest.class));
        // Act & Assert: Expect a 400 Bad Request response
        mockMvc.perform(post("/api/accounts/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\":\"Bad request\",\"message\":\"Insufficient funds.\",\"status\":400}"));
    }
//
//    @Test
//    @WithMockUser(username = "testuser")
//    public void testTransfer_Success() throws Exception {
//        // Arrange
//        User dummyUser = createDummyUser("testuser", 1);
//        when(userDAO.findByUsername("testuser")).thenReturn(dummyUser);
//
//        // User owns the source account 10.
//        Account sourceAccount = new Account();
//        sourceAccount.setId(10);
//        sourceAccount.setUserId(1);
//        when(accountDAO.findByUserId(1)).thenReturn(Collections.singletonList(sourceAccount));
//
//        when(accountDAO.getBalance(10)).thenReturn(BigDecimal.valueOf(300));
//        when(accountDAO.getBalance(20)).thenReturn(BigDecimal.valueOf(100));
//
//        doNothing().when(accountDAO).updateBalance(10, BigDecimal.valueOf(250));
//        doNothing().when(accountDAO).updateBalance(20, BigDecimal.valueOf(150));
//        doNothing().when(transactionDAO).logTransaction(10, "transfer", BigDecimal.valueOf(50),
//                "Transfer to account 20");
//        doNothing().when(transactionDAO).logTransaction(20, "deposit", BigDecimal.valueOf(50),
//                "Transfer from account 10");
//
//        TransferRequest transferRequest = new TransferRequest();
//        transferRequest.setFromAccountId(10);
//        transferRequest.setToAccountId(20);
//        transferRequest.setAmount(BigDecimal.valueOf(50));
//        transferRequest.setDescription("Transfer to account 20");
//
//        // Act & Assert
//        mockMvc.perform(post("/api/accounts/transfer")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(transferRequest)))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Transfer successful."));
//    }

    @Test
    @WithMockUser(username = "testuser")
    public void testTransfer_InsufficientFunds() throws Exception {
        // Arrange
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setSourceAccountId(10L);
        transferRequest.setDestinationAccountId(20L);
        transferRequest.setAmount(BigDecimal.valueOf(100));
        doThrow(new RuntimeException("Insufficient funds.")).when(accountService).transfer(any(TransferRequest.class));
        // Act & Assert: Expect a 400 Bad Request response
        mockMvc.perform(post("/api/accounts/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\":\"Bad request\",\"message\":\"Insufficient funds.\",\"status\":400}"));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testTransfer_UnauthorizedSourceAccount() throws Exception {
        // Arrange
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setSourceAccountId(20L);
        transferRequest.setDestinationAccountId(30L);
        transferRequest.setAmount(BigDecimal.valueOf(50));
        doThrow(new RuntimeException("Unauthorized: You don't own this account.")).when(accountService).transfer(any(TransferRequest.class));
        // Act & Assert: Expect a 403 Forbidden response.
        mockMvc.perform(post("/api/accounts/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isForbidden())
                .andExpect(content().json("{\"error\":\"Forbidden\",\"message\":\"Unauthorized: You don't own this account.\",\"status\":403}"));
    }


}
