package com.banking.backend.repository;

import com.banking.backend.model.Account;
import com.banking.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    List<Account> findByUser(User user);
    Optional<Account> findByIban(String iban); // ← added
    Optional<Account> findByid(UUID id);
    //Get user by IBAN
}

