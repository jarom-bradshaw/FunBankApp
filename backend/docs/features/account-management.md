# Account Management Feature Implementation

## Overview
Account management system for handling user financial accounts and account linking with Plaid integration.

## Implementation Details

### 1. Account Controller
```java
@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    @Autowired
    private AccountService accountService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<AccountDTO>>> getAllAccounts() {
        try {
            List<AccountDTO> accounts = accountService.getAllAccounts();
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Accounts retrieved successfully", accounts));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<AccountDTO>> createAccount(@Valid @RequestBody AccountCreateDTO request) {
        try {
            AccountDTO account = accountService.createAccount(request);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Account created successfully", account));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }
}
```

### 2. Account Linking Controller
```java
@RestController
@RequestMapping("/api/account-linking")
public class AccountLinkingController {
    @Autowired
    private AccountLinkingService accountLinkingService;
    
    @PostMapping("/plaid")
    public ResponseEntity<ApiResponse<LinkTokenResponseDTO>> createLinkToken() {
        try {
            LinkTokenResponseDTO response = accountLinkingService.createLinkToken();
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Link token created", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }
    
    @PostMapping("/plaid/callback")
    public ResponseEntity<ApiResponse<AccountDTO>> handlePlaidCallback(@RequestBody PlaidCallbackDTO callback) {
        try {
            AccountDTO account = accountLinkingService.handlePlaidCallback(callback);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Account linked successfully", account));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }
}
```

### 3. DTOs
```java
public record AccountDTO(
    Long id,
    String name,
    String type,
    BigDecimal balance,
    String currency,
    String status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}

public record AccountCreateDTO(
    @NotBlank String name,
    @NotBlank String type,
    @NotNull BigDecimal initialBalance,
    @NotBlank String currency
) {}

public record LinkTokenResponseDTO(
    String linkToken,
    LocalDateTime expiresAt
) {}

public record PlaidCallbackDTO(
    String publicToken,
    String metadata
) {}
```

### 4. Service Implementation
```java
@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepository accountRepository;
    
    @Override
    public List<AccountDTO> getAllAccounts() {
        return accountRepository.findAll().stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public AccountDTO createAccount(AccountCreateDTO request) {
        Account account = new Account();
        account.setName(request.name());
        account.setType(request.type());
        account.setBalance(request.initialBalance());
        account.setCurrency(request.currency());
        account.setStatus("ACTIVE");
        
        Account savedAccount = accountRepository.save(account);
        return mapToDTO(savedAccount);
    }
}

@Service
public class AccountLinkingServiceImpl implements AccountLinkingService {
    @Autowired
    private PlaidClient plaidClient;
    
    @Override
    public LinkTokenResponseDTO createLinkToken() {
        LinkTokenCreateRequest request = new LinkTokenCreateRequest()
            .user(new LinkTokenCreateRequestUser()
                .clientUserId(UUID.randomUUID().toString()))
            .clientName("FunBank App")
            .products(Arrays.asList("auth", "transactions"))
            .countryCodes(Arrays.asList("US"))
            .language("en");
            
        LinkTokenCreateResponse response = plaidClient.linkTokenCreate(request);
        return new LinkTokenResponseDTO(
            response.getLinkToken(),
            LocalDateTime.now().plusHours(24)
        );
    }
}
```

## Testing
- Unit tests for account CRUD operations
- Integration tests for Plaid account linking
- Transaction sync tests
- Error handling tests

## Security Considerations
1. Secure storage of Plaid credentials
2. Encryption of sensitive account data
3. Access control for account operations
4. Audit logging for account changes

## Next Steps
1. Implement account balance history tracking
2. Add support for multiple currencies
3. Implement account statement generation
4. Add account alerts and notifications 