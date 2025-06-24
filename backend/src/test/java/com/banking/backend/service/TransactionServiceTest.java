package com.banking.backend.service;

import com.banking.backend.dto.ATMDTO;
import com.banking.backend.model.Account;
import com.banking.backend.model.Transaction;
import com.banking.backend.repository.AccountRepository;
import com.banking.backend.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Account fromAccount;
    private Account toAccount;
    private Transaction transaction;
    private UUID fromAccountId;
    private UUID toAccountId;

    @BeforeEach
    void setUp() {
        fromAccountId = UUID.randomUUID();
        toAccountId = UUID.randomUUID();

        fromAccount = new Account();
        fromAccount.setId(fromAccountId);
        fromAccount.setBalance(1000.0);

        toAccount = new Account();
        toAccount.setId(toAccountId);
        toAccount.setBalance(500.0);

        transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setAmount(200.0);
        transaction.setDateOfExecution(LocalDateTime.now());
    }

    @Test
    void makeTransaction_WithValidAccountsAndSufficientBalance_ShouldProcessSuccessfully() {
        // Given
        when(accountRepository.findById(fromAccountId)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(toAccountId)).thenReturn(Optional.of(toAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(fromAccount);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        // When
        Transaction result = transactionService.makeTransaction(transaction);

        // Then
        assertNotNull(result);
        assertEquals(800.0, fromAccount.getBalance()); // 1000 - 200
        assertEquals(700.0, toAccount.getBalance());   // 500 + 200

        verify(accountRepository, times(1)).findById(fromAccountId);
        verify(accountRepository, times(1)).findById(toAccountId);
        verify(accountRepository, times(2)).save(any(Account.class)); // Save both accounts
        verify(transactionRepository, times(1)).save(transaction);
    }

    @Test
    void makeTransaction_WithInsufficientBalance_ShouldThrowException() {
        // Given
        transaction.setAmount(1500.0); // More than available balance (1000)
        when(accountRepository.findById(fromAccountId)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(toAccountId)).thenReturn(Optional.of(toAccount));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.makeTransaction(transaction);
        });

        assertEquals("Insufficient funds in account", exception.getMessage());
        assertEquals(1000.0, fromAccount.getBalance()); // Balance should remain unchanged
        assertEquals(500.0, toAccount.getBalance());    // Balance should remain unchanged

        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void makeTransaction_WithInvalidFromAccount_ShouldThrowException() {
        // Given
        when(accountRepository.findById(fromAccountId)).thenReturn(Optional.empty());
        when(accountRepository.findById(toAccountId)).thenReturn(Optional.of(toAccount));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.makeTransaction(transaction);
        });

        assertEquals("Source account not found", exception.getMessage());
        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void makeTransaction_WithInvalidToAccount_ShouldThrowException() {
        // Given
        when(accountRepository.findById(fromAccountId)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(toAccountId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.makeTransaction(transaction);
        });

        assertEquals("Destination account not found", exception.getMessage());
        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void makeTransaction_WithBothInvalidAccounts_ShouldThrowException() {
        // Given
        when(accountRepository.findById(fromAccountId)).thenReturn(Optional.empty());
        when(accountRepository.findById(toAccountId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.makeTransaction(transaction);
        });

        assertEquals("Source account not found", exception.getMessage());
        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void makeTransaction_WithZeroAmount_ShouldThrowException() {
    // Given
    transaction.setAmount(0.0);

    // When & Then
    assertThrows(IllegalArgumentException.class, () -> {
        transactionService.makeTransaction(transaction);
    });
    }

    @Test
    void getTransactionsForAccount_WhenTransactionsExist_ShouldReturnTransactionList() {
        // Given
        Account account = new Account();
        account.setId(fromAccountId);

        Transaction transaction1 = new Transaction();
        transaction1.setFromAccount(account);
        transaction1.setToAccount(toAccount);
        transaction1.setAmount(100.0);

        Transaction transaction2 = new Transaction();
        transaction2.setFromAccount(toAccount);
        transaction2.setToAccount(account);
        transaction2.setAmount(50.0);

        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);

        when(accountRepository.findById(fromAccountId)).thenReturn(Optional.of(account));
        when(transactionRepository.findByFromAccountOrToAccount(account, account)).thenReturn(transactions);

        // When
        List<Transaction> result = transactionService.getTransactionsForAccount(fromAccountId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(transaction1));
        assertTrue(result.contains(transaction2));
        verify(transactionRepository, times(1)).findByFromAccountOrToAccount(account, account);
    }

    @Test
    void getTransactionsForAccount_WhenNoTransactionsExist_ShouldReturnEmptyList() {
        // Given
        Account account = new Account();
        account.setId(fromAccountId);

        when(accountRepository.findById(fromAccountId)).thenReturn(Optional.of(account));
        when(transactionRepository.findByFromAccountOrToAccount(account, account))
                .thenReturn(Arrays.asList());

        // When
        List<Transaction> result = transactionService.getTransactionsForAccount(fromAccountId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(transactionRepository, times(1)).findByFromAccountOrToAccount(account, account);
    }

    @Test
    void getTransactionsForAccount_WithNullAccountId_ShouldHandleGracefully() {
        // Given
        when(accountRepository.findById(null)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.getTransactionsForAccount(null);
        });

        assertEquals("Invalid account ID", exception.getMessage());
        verify(transactionRepository, never()).findByFromAccountOrToAccount(any(), any());
    }
    @Test
    void createWithdrawal_WithValidAmountAndBalance_ShouldProcessSuccessfully() {
        // Given
        ATMDTO dto = new ATMDTO();
        dto.setIBAN("NL01BANK0123456789");
        dto.setAmount(300.0);

        Account account = new Account();
        account.setIban(dto.getIBAN());
        account.setBalance(500.0);

        when(accountRepository.findByIban(dto.getIBAN())).thenReturn(Optional.of(account));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArguments()[0]);

        // When
        Transaction result = transactionService.createWithdrawal(dto);

        // Then
        assertNotNull(result);
        assertEquals(account, result.getFromAccount());
        assertEquals(account, result.getToAccount());
        assertTrue(result.getDateOfExecution() != null);
        assertEquals(200.0, account.getBalance()); // 500 - 300

        verify(accountRepository).save(account);
        verify(transactionRepository).save(any(Transaction.class));
    }
    @Test
    void createDeposit_WithValidAmount_ShouldIncreaseBalance() {
        // Given
        ATMDTO dto = new ATMDTO();
        dto.setIBAN("NL01BANK0123456789");
        dto.setAmount(400.0);

        Account account = new Account();
        account.setIban(dto.getIBAN());
        account.setBalance(600.0);

        when(accountRepository.findByIban(dto.getIBAN())).thenReturn(Optional.of(account));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArguments()[0]);

        // When
        Transaction result = transactionService.createDeposit(dto);

        // Then
        assertNotNull(result);
        assertEquals(account, result.getFromAccount());
        assertEquals(account, result.getToAccount());
        assertEquals(1000.0, account.getBalance()); // 600 + 400

        verify(accountRepository).save(account);
        verify(transactionRepository).save(any(Transaction.class));
    }
    @Test
    void createWithdrawal_WithZeroAmount_ShouldThrowException() {
        ATMDTO dto = new ATMDTO();
        dto.setIBAN("NL01BANK0123456789");
        dto.setAmount(0.0);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.createWithdrawal(dto);
        });

        assertEquals("Withdrawal amount must be greater than zero", ex.getMessage());
        verify(accountRepository, never()).findByIban(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void createWithdrawal_WithNegativeAmount_ShouldThrowException() {
        ATMDTO dto = new ATMDTO();
        dto.setIBAN("NL01BANK0123456789");
        dto.setAmount(-50.0);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.createWithdrawal(dto);
        });

        assertEquals("Withdrawal amount must be greater than zero", ex.getMessage());
        verify(accountRepository, never()).findByIban(any());
        verify(transactionRepository, never()).save(any());
    }
    @Test
    void createDeposit_WithZeroAmount_ShouldThrowException() {
        ATMDTO dto = new ATMDTO();
        dto.setIBAN("NL01BANK0123456789");
        dto.setAmount(0.0);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.createDeposit(dto);
        });

        assertEquals("Withdrawal amount must be greater than zero", ex.getMessage());
        verify(accountRepository, never()).findByIban(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void createDeposit_WithNegativeAmount_ShouldThrowException() {
        ATMDTO dto = new ATMDTO();
        dto.setIBAN("NL01BANK0123456789");
        dto.setAmount(-200.0);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.createDeposit(dto);
        });

        assertEquals("Withdrawal amount must be greater than zero", ex.getMessage());
        verify(accountRepository, never()).findByIban(any());
        verify(transactionRepository, never()).save(any());
    }


}