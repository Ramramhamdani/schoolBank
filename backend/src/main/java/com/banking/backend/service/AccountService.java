package com.banking.backend.service;

import com.banking.backend.model.Account;
import com.banking.backend.model.User;
import com.banking.backend.repository.AccountRepository;
import com.banking.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import java.util.List;
import java.util.UUID;
import java.time.LocalDate; 

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    public Optional<Account> getAccountByIban(String iban) {
    return accountRepository.findByIban(iban);
}
    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    public List<Account> getAccountsByUserId(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return accountRepository.findByUser(user);
    }

    public Account getAccountById(UUID accountId) {
        return accountRepository.findById(accountId).orElse(null);
    }

    // ── NEW METHOD #1: return all accounts ──
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    // ── NEW METHOD #2: create from request ──
    public Account createAccountByRequest(Account accountEntity, String customerEmail) {
        // Look up the user by email (the DTO field was previously misnamed)
        User user = userRepository.findByEmail(customerEmail)
            .orElseThrow(() -> new IllegalArgumentException("No user found with identifier: " + customerEmail));
        // 2) Set the owner
        accountEntity.setUser(user);
        // 3) Set defaults
        accountEntity.setDateOfOpening(LocalDate.now());
        accountEntity.setAbsoluteLimit(0.0);
        accountEntity.setActive(true);

        // Generate a random IBAN for testing purposes
        accountEntity.setIban(generateTestIban());

        // 4) Save
        return accountRepository.save(accountEntity);
    }

    // ── NEW METHOD #3: delete an account ──
    public void deactivateAccount(UUID accountId) {
        Optional<Account> account = accountRepository.findById(accountId);
        account.get().setActive(false);
        accountRepository.save(account.get());
    }
    private String generateTestIban() {
        // Simple IBAN generation for tests
        return "NL" + String.format("%02d", (int)(Math.random() * 99)) +
                "BANK" + String.format("%010d", (long)(Math.random() * 10000000000L));
    }

    public UUID getUserIdByIban(String iban) {
        return accountRepository.findByIban(iban)
                .map(account -> account.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("Account or User not found for IBAN: " + iban));
    }
}