package com.banking.backend.service;

import com.banking.backend.model.Account;
import com.banking.backend.model.User;
import com.banking.backend.repository.AccountRepository;
import com.banking.backend.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AccountService accountService;

    private Account testAccount;
    private User testUser;
    private UUID testUserId;
    private UUID testAccountId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testAccountId = UUID.randomUUID();

        testUser = new User();
        testUser.setId(testUserId);
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEmail("testuser@example.com");

        testAccount = new Account();
        testAccount.setId(testAccountId);
        testAccount.setUser(testUser);
        testAccount.setBalance(1000.0);
    }

    @Test
    void createAccount_ShouldReturnCreatedAccount() {
        // Given
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        // When
        Account result = accountService.createAccount(testAccount);

        // Then
        assertNotNull(result);
        assertEquals(testAccount.getId(), result.getId());
        assertEquals(testAccount.getUser().getId(), result.getUser().getId());
        assertEquals(testAccount.getBalance(), result.getBalance());
        verify(accountRepository, times(1)).save(testAccount);
    }

    @Test
    void createAccount_WithNullAccount_ShouldHandleGracefully() {
        // Given
        when(accountRepository.save(null)).thenThrow(new IllegalArgumentException("Account cannot be null"));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            accountService.createAccount(null);
        });
        verify(accountRepository, times(1)).save(null);
    }

    @Test
void getAccountsByUserId_WhenUserHasAccounts_ShouldReturnAccountList() {
    // Given
    Account secondAccount = new Account();
    secondAccount.setId(UUID.randomUUID());
    secondAccount.setUser(testUser);
    secondAccount.setBalance(500.0);

    List<Account> userAccounts = Arrays.asList(testAccount, secondAccount);
    when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
    when(accountRepository.findByUser(eq(testUser))).thenReturn(userAccounts);

    // When
    List<Account> result = accountService.getAccountsByUserId(testUser.getId());

    // Then
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(testUserId, result.get(0).getUser().getId());
    assertEquals(testUserId, result.get(1).getUser().getId());
    verify(accountRepository, times(1)).findByUser(testUser);
}

@Test
void getAccountsByUser_WhenUserHasNoAccounts_ShouldReturnEmptyList() {
    // Given
    User userWithNoAccounts = new User();
    userWithNoAccounts.setId(UUID.randomUUID());
    when(userRepository.findById(userWithNoAccounts.getId())).thenReturn(Optional.of(userWithNoAccounts));
    when(accountRepository.findByUser(eq(userWithNoAccounts))).thenReturn(Arrays.asList());

    // When
    List<Account> result = accountService.getAccountsByUserId(userWithNoAccounts.getId());

    // Then
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(accountRepository, times(1)).findByUser(userWithNoAccounts);
}

    @Test
    void getAccountById_WhenAccountExists_ShouldReturnAccount() {
        // Given
        when(accountRepository.findById(testAccountId)).thenReturn(Optional.of(testAccount));

        // When
        Account result = accountService.getAccountById(testAccountId);

        // Then
        assertNotNull(result);
        assertEquals(testAccountId, result.getId());
        assertEquals(testAccount.getBalance(), result.getBalance());
        verify(accountRepository, times(1)).findById(testAccountId);
    }

    @Test
    void getAccountById_WhenAccountDoesNotExist_ShouldReturnNull() {
        // Given
        UUID nonExistentAccountId = UUID.randomUUID();
        when(accountRepository.findById(nonExistentAccountId)).thenReturn(Optional.empty());

        // When
        Account result = accountService.getAccountById(nonExistentAccountId);

        // Then
        assertNull(result);
        verify(accountRepository, times(1)).findById(nonExistentAccountId);
    }

    @Test
    void getAccountById_WithNullId_ShouldHandleGracefully() {
        // Given
        when(accountRepository.findById(null)).thenReturn(Optional.empty());

        // When
        Account result = accountService.getAccountById(null);

        // Then
        assertNull(result);
        verify(accountRepository, times(1)).findById(null);
    }
}