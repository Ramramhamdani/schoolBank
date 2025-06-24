package com.banking.backend.repository;

import com.banking.backend.model.Account;
import com.banking.backend.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    // Spring Data JPA will implement this for you
    List<Transaction> findByFromAccountOrToAccount(Account fromAccount, Account toAccount);
    List<Transaction> findByToAccount(Account toAccount);
    List<Transaction> findByFromAccount(Account fromAccount);
}