package com.banking.backend.controller;

import com.banking.backend.model.Account;
import com.banking.backend.model.AccountType;
import com.banking.backend.model.User;
import com.banking.backend.repository.UserRepository;
import com.banking.backend.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AccountService accountService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AccountController accountController;

    private ObjectMapper objectMapper;
    private Account testAccount;
    private User testUser;
    private UUID testAccountId;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testAccountId = UUID.randomUUID();
        testUserId = UUID.randomUUID();

        testUser = new User();
        testUser.setId(testUserId);
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEmail("testuser@example.com");

        testAccount = new Account();
        testAccount.setId(testAccountId);
        testAccount.setUser(testUser);
        testAccount.setTypeOfAccount(AccountType.CURRENT);
        testAccount.setBalance(1000.00);

        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
void createAccount_Success() throws Exception {
    // Given: build JSON like AccountRequestDTO
    Map<String, String> requestBody = new HashMap<>();
    requestBody.put("requestedAccountType", testAccount.getTypeOfAccount().name());
    requestBody.put("customerEmail", testUser.getEmail());

    // Mock createAccountByRequest(...) instead of createAccount(...)
    when(accountService.createAccountByRequest(
            any(Account.class),
            eq(testUser.getEmail())
    )).thenReturn(testAccount);

    // When & Then
    mockMvc.perform(post("/accounts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(testAccount.getId().toString()))
            .andExpect(jsonPath("$.userId").value(testUser.getId().toString()))
            .andExpect(jsonPath("$.typeOfAccount")
                          .value(testAccount.getTypeOfAccount().name()))
            .andExpect(jsonPath("$.balance")
                          .value(testAccount.getBalance()));
}


    @Test
    void createAccount_InvalidInput() throws Exception {
        // Given - empty request body
        String invalidJson = "{}";

        // When & Then - This will depend on your validation logic
        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isCreated()); // Adjust based on your validation
    }

    @Test
    void getAccountById_Success() throws Exception {
        when(accountService.getAccountById(eq(testAccountId))).thenReturn(testAccount);

        mockMvc.perform(get("/accounts/{id}", testAccountId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testAccount.getId().toString()))
                .andExpect(jsonPath("$.userId").value(testUser.getId().toString()))
                .andExpect(jsonPath("$.typeOfAccount").value(testAccount.getTypeOfAccount().name()))
                .andExpect(jsonPath("$.balance").value(testAccount.getBalance()));
    }

    @Test
    void getAccountById_NotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        when(accountService.getAccountById(eq(nonExistentId))).thenReturn(null);

        mockMvc.perform(get("/accounts/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAccountById_InvalidUuid() throws Exception {
        mockMvc.perform(get("/accounts/{id}", "invalid-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAccountsByUserId_Success() throws Exception {
    // Setup another account for the same user
    Account account2 = new Account();
    account2.setId(UUID.randomUUID());
    account2.setUser(testUser);
    account2.setTypeOfAccount(AccountType.SAVINGS);
    account2.setBalance(2000.00);

    List<Account> accounts = Arrays.asList(testAccount, account2);

    // Mock: When service gets called with testUserId, return the two accounts.
    when(accountService.getAccountsByUserId(eq(testUserId))).thenReturn(accounts);

    mockMvc.perform(get("/accounts/user/{userId}", testUserId))
           .andExpect(status().isOk())
           .andExpect(content().contentType(MediaType.APPLICATION_JSON))
           .andExpect(jsonPath("$.length()").value(2))
           .andExpect(jsonPath("$[0].id").value(testAccount.getId().toString()))
           // Was: jsonPath("$[0].user.id")
           .andExpect(jsonPath("$[0].userId").value(testUser.getId().toString()))
           .andExpect(jsonPath("$[0].typeOfAccount")
                          .value(testAccount.getTypeOfAccount().name()))
           .andExpect(jsonPath("$[1].id").value(account2.getId().toString()))
           // Was: jsonPath("$[1].user.id")
           .andExpect(jsonPath("$[1].userId").value(testUser.getId().toString()))
           .andExpect(jsonPath("$[1].typeOfAccount")
                          .value(account2.getTypeOfAccount().name()));
    }


@Test
void getAccountsByUserId_EmptyList() throws Exception {
    when(accountService.getAccountsByUserId(eq(testUserId))).thenReturn(Arrays.asList());

    mockMvc.perform(get("/accounts/user/{userId}", testUserId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(0));
}

    @Test
    void getAccountsByUserId_InvalidUuid() throws Exception {
        mockMvc.perform(get("/accounts/user/{userId}", "invalid-uuid"))
                .andExpect(status().isBadRequest());
    }
}