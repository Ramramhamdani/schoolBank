package com.banking.backend.service;

import com.banking.backend.dto.ATMDTO;
import com.banking.backend.model.Account;
import com.banking.backend.model.Transaction;
import com.banking.backend.model.TransactionType;
import com.banking.backend.repository.AccountRepository;
import com.banking.backend.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    public Transaction makeTransaction(Transaction transaction) {
        validateTransactionFields(transaction);

        UUID fromId = transaction.getFromAccount().getId();
        UUID toId = transaction.getToAccount().getId();
        double amount = transaction.getAmount();

        if (fromId.equals(toId)) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }

        Account from = getAccountById(fromId, "Source account not found");
        Account to = getAccountById(toId, "Destination account not found");

        if (from.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient funds in account");
        }

        transaction.setFromAccount(from);
        transaction.setToAccount(to);
        transaction.setDateOfExecution(LocalDateTime.now());
        transactionRepository.save(transaction);

        updateBalance(from, -amount);
        updateBalance(to, amount);

        return transaction;
    }

    public Transaction createWithdrawal(ATMDTO dto) {
        return handleATMTransaction(dto, -dto.getAmount(), "Withdrawal amount must be greater than zero");
    }

    public Transaction createDeposit(ATMDTO dto) {
        return handleATMTransaction(dto, dto.getAmount(), "Deposit amount must be greater than zero");
    }

    public List<Transaction> getTransactionsForAccount(UUID accountId) {
        Account account = getAccountById(accountId, "Invalid account ID");
        return transactionRepository.findByFromAccountOrToAccount(account, account);
    }
    public List<Transaction> getTransactionsByToAccount(UUID accountId) {
        Account account = getAccountById(accountId, "Invalid account ID");
        return transactionRepository.findByToAccount(account);
    }
    public List<Transaction> getTransactionsByFromAccount(UUID accountId) {
        Account account = getAccountById(accountId, "Invalid account ID");
        return transactionRepository.findByFromAccount(account);
    }

    // Helper Methods

    private void validateTransactionFields(Transaction tx) {
        if (tx == null || tx.getFromAccount() == null || tx.getToAccount() == null) {
            throw new IllegalArgumentException("Missing required transaction fields");
        }

        if (tx.getAmount() <= 0) {
            throw new IllegalArgumentException(tx.getAmount() == 0
                    ? "Transaction amount must be greater than zero"
                    : "Transaction amount must be positive");
        }
    }

    private Account getAccountById(UUID id, String errorMsg) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(errorMsg));
    }

    private Account getAccountByIban(String iban) {
        return accountRepository.findByIban(iban)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
    }

    private void updateBalance(Account account, double delta) {
        account.setBalance(account.getBalance() + delta);
        accountRepository.save(account);
    }

    private Transaction handleATMTransaction(ATMDTO dto, double amountDelta, String invalidAmountMsg) {
        if (dto == null || dto.getIBAN() == null) {
            throw new IllegalArgumentException("Missing required transaction fields");
        }

        if (Math.abs(amountDelta) <= 0) {
            throw new IllegalArgumentException(invalidAmountMsg);
        }

        Account account = getAccountByIban(dto.getIBAN());

        if (amountDelta < 0 && account.getBalance() < Math.abs(amountDelta)) {
            throw new IllegalArgumentException("Insufficient funds in account");
        }

        Transaction transaction = new Transaction();
        transaction.setFromAccount(account);
        transaction.setToAccount(account);
        if (amountDelta > 0) {
        transaction.setTypeOfTransaction(TransactionType.DEPOSIT);
        }
        if (amountDelta < 0) {
            transaction.setTypeOfTransaction(TransactionType.WITHDRAWAL);
        }
        transaction.setDateOfExecution(LocalDateTime.now());
        transaction.setAmount(amountDelta);

        transactionRepository.save(transaction);
        updateBalance(account, amountDelta);

        return transaction;
    }
}
