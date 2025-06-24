package com.banking.backend.controller;

import com.banking.backend.dto.AccountDTO;
import com.banking.backend.dto.AccountRequestDTO;
import com.banking.backend.mapper.AccountMapper;
import com.banking.backend.model.Account;
import com.banking.backend.model.AccountType;
import com.banking.backend.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService service) {
        this.accountService = service;
    }

    // GET /accounts → return all accounts
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AccountDTO>> getAllAccounts() {
        List<Account> entities = accountService.getAllAccounts();
        List<AccountDTO> dtos = entities.stream()
                                         .map(AccountMapper::toDTO)
                                         .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // POST /accounts → create a new account from AccountRequestDTO
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AccountDTO> createAccount(@RequestBody AccountRequestDTO requestDto) {
        System.out.println("Auth: " + SecurityContextHolder.getContext().getAuthentication());
        // 1) Convert AccountRequestDTO → Account entity
        Account toSave = new Account();
        toSave.setTypeOfAccount(
            requestDto.getRequestedAccountType() != null
                ? AccountType.valueOf(requestDto.getRequestedAccountType())
                : null
        );
        // look up User by Email in service
        Account saved = accountService.createAccountByRequest(
            toSave, requestDto.getCustomerEmail()
        );
        // 2) Convert saved entity → AccountDTO
        AccountDTO response = AccountMapper.toDTO(saved);
        return ResponseEntity.status(201).body(response);
    }

    // GET /users/{userId}/accounts → list accounts for that user
    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AccountDTO>> getAccountsByUser_Legacy(@PathVariable String userId) {
    UUID uuid;
    try {
        uuid = UUID.fromString(userId);
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().build();
    }
    List<Account> accounts = accountService.getAccountsByUserId(uuid);
    List<AccountDTO> dtos = accounts.stream()
        .map(AccountMapper::toDTO)
        .collect(Collectors.toList());
    return ResponseEntity.ok(dtos);
}

    // GET /accounts/{id} → get one account
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AccountDTO> getAccountById(@PathVariable UUID id) {
        Account entity = accountService.getAccountById(id);
        if (entity == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(AccountMapper.toDTO(entity));
    }

    // DELETE /accounts/{id} →  soft delete an account
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteAccount(@PathVariable UUID id) {
        accountService.deactivateAccount(id);
        return ResponseEntity.noContent().build();
    }
}
