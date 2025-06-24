package com.banking.backend.controller;

import com.banking.backend.dto.ATMDTO;
import com.banking.backend.dto.TransactionDTO;
import com.banking.backend.mapper.TransactionMapper;
import com.banking.backend.model.Account;
import com.banking.backend.model.Transaction;
import com.banking.backend.model.User;
import com.banking.backend.service.AccountService;
import com.banking.backend.service.TransactionService;
import com.banking.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class TransactionController {

    private final TransactionService transactionService;
    private final AccountService accountService;
    private final UserService userService;

    public TransactionController(TransactionService transactionService,
                                 AccountService accountService,
                                 UserService userService) {
        this.transactionService = transactionService;
        this.accountService = accountService;
        this.userService = userService;
    }

    /**
     * POST /transactions
     *
     * • If fromIban == toIban                 → 400 + "Cannot transfer to the same account"
     * • If amount == 0                        → 400 + "Transaction amount must be greater than zero"
     * • If amount < 0                         → 400 + "Transaction amount must be positive"
     * • If source account lookup fails        → 400 + "Source account not found"
     * • If destination lookup fails           → 400 + "Destination account not found"
     * • If user lookup fails                  → 400 + "Performing user not found"
     * • Else delegate to transactionService.makeTransaction(...)
     *   – If service throws IllegalArgumentException("Insufficient funds in account")
     *     → 400 + "Insufficient funds in account"
     * • On success → 201 + JSON(body = TransactionDTO)
     */
    @PostMapping(
        value = "/transactions",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TransactionDTO> createTransaction(@RequestBody TransactionDTO dto) {
        // 1) Reject “same account”
        if (dto.getFromIban() != null && dto.getFromIban().equals(dto.getToIban())) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }

        // 2) Distinguish zero vs. negative
        if (dto.getAmount() == 0) {
            throw new IllegalArgumentException("Transaction amount must be greater than zero");
        }
        if (dto.getAmount() < 0) {
            throw new IllegalArgumentException("Transaction amount must be positive");
        }

        // 3) Map DTO → entity (accounts and user will be set next)
        Transaction txEntity = TransactionMapper.toEntity(dto);

        // 4) Lookup “from” account
        Account fromAcct = accountService
                .getAccountByIban(dto.getFromIban())
                .orElseThrow(() -> new IllegalArgumentException("Source account not found"));

        // 5) Lookup “to” account
        Account toAcct = accountService
                .getAccountByIban(dto.getToIban())
                .orElseThrow(() -> new IllegalArgumentException("Destination account not found"));

        txEntity.setFromAccount(fromAcct);
        txEntity.setToAccount(toAcct);

        // 6) Lookup performing user if provided
        if (dto.getPerformingUserId() != null) {
            User user = userService.getUserById(dto.getPerformingUserId().toString());
            UUID userId = user.getId();
            UUID fromAccUserid = accountService.getUserIdByIban(dto.getFromIban());
            //Check if performing ID is same owner of sender IBAN
            if(userId != fromAccUserid){
                throw new IllegalArgumentException("Provide a valid IBAN");
            }
            if (user == null) {
                throw new IllegalArgumentException("Performing user not found");
            }
            txEntity.setPerformingUser(user);
        }

        // 7) Delegate to service
        Transaction saved = transactionService.makeTransaction(txEntity);

        // 8) Map entity → DTO for response
        TransactionDTO responseDto = TransactionMapper.toDTO(saved);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(responseDto);
    }

    /**
     * GET /accounts/{accountId}/transactions
     *
     * • If accountId is not a valid UUID → 400
     * • Otherwise always returns 200 + JSON array (possibly empty) of TransactionDTO
     */
    @GetMapping(
        value = "/accounts/{accountId}/transactions",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TransactionDTO>> getAccountTransactions(@PathVariable UUID accountId) {
        List<Transaction> list = transactionService.getTransactionsForAccount(accountId);
        List<TransactionDTO> dtos = list.stream()
                                        .map(TransactionMapper::toDTO)
                                        .collect(Collectors.toList());

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(dtos);
    }

    /**
     * Any IllegalArgumentException from above → 400 + plain‐text message
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.TEXT_PLAIN)
                .body(ex.getMessage());
    }
    /**
     * POST /atm/withdraw
     * Performs ATM withdrawal.
     */
    @PostMapping(
            value = "/atm/withdraw",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TransactionDTO> withdraw(@RequestBody ATMDTO dto) {
        Transaction transaction = transactionService.createWithdrawal(dto);
        TransactionDTO responseDto = TransactionMapper.toDTO(transaction);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(responseDto);
    }

    /**
     * POST /atm/deposit
     * Performs ATM deposit.
     */
    @PostMapping(
            value = "/atm/deposit",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TransactionDTO> deposit(@RequestBody ATMDTO dto) {
        Transaction transaction = transactionService.createDeposit(dto);
        TransactionDTO responseDto = TransactionMapper.toDTO(transaction);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(responseDto);
    }
    @GetMapping(value = "/accounts/{accountId}/received", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TransactionDTO>> getTransactionsReceivedByAccount(@PathVariable UUID accountId) {
        List<Transaction> receivedTransactions = transactionService.getTransactionsByToAccount(accountId);
        List<TransactionDTO> dtos = receivedTransactions.stream()
                .map(TransactionMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(dtos);
    }

    @GetMapping(value = "/accounts/{accountId}/sent", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TransactionDTO>> getTransactionsSentFromAccount(@PathVariable UUID accountId) {
        List<Transaction> sentTransactions = transactionService.getTransactionsByFromAccount(accountId);
        List<TransactionDTO> dtos = sentTransactions.stream()
                .map(TransactionMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(dtos);
    }

}
