package com.banking.backend.mapper;

import com.banking.backend.dto.TransactionDTO;
import com.banking.backend.model.Transaction;
import com.banking.backend.model.TransactionType;

public class TransactionMapper {

    // Converts Transaction entity to TransactionDTO for API responses
    public static TransactionDTO toDTO(Transaction transaction) {
        if (transaction == null) return null;
        return new TransactionDTO(
            transaction.getId(),
            transaction.getFromAccount() != null ? transaction.getFromAccount().getIban() : null,
            transaction.getToAccount() != null ? transaction.getToAccount().getIban() : null,
            transaction.getAmount(),
            transaction.getTypeOfTransaction() != null ? transaction.getTypeOfTransaction().name() : null,
            transaction.getDateOfExecution(),
            transaction.getPerformingUser() != null ? transaction.getPerformingUser().getId() : null,
            transaction.getDescription()
        );
    }

    // Converts TransactionDTO to Transaction entity for saving to database
    // (Account and User objects must be set in the service layer after fetching from repo)
    public static Transaction toEntity(TransactionDTO dto) {
        if (dto == null) return null;
        Transaction transaction = new Transaction();
        transaction.setId(dto.getId());
        // Set accounts and user in the service after fetching by IBAN or ID
        transaction.setAmount(dto.getAmount());
        if (dto.getTypeOfTransaction() != null) {
            transaction.setTypeOfTransaction(TransactionType.valueOf(dto.getTypeOfTransaction()));
        }
        transaction.setDateOfExecution(dto.getDateOfExecution());
        transaction.setDescription(dto.getDescription());
        return transaction;
    }

}