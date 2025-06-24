package com.banking.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.UUID;

public class TransactionDTO {
    private UUID id;
    private String fromIban;
    private String toIban;
    private double amount;
    private String typeOfTransaction;

    // Always print exactly six digits of fractional seconds
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    private LocalDateTime dateOfExecution;

    private UUID performingUserId;
    private String description;

    public TransactionDTO() {}

    public TransactionDTO(
        UUID id,
        String fromIban,
        String toIban,
        double amount,
        String typeOfTransaction,
        LocalDateTime dateOfExecution,
        UUID performingUserId,
        String description
    ) {
        this.id = id;
        this.fromIban = fromIban;
        this.toIban = toIban;
        this.amount = amount;
        this.typeOfTransaction = typeOfTransaction;
        this.dateOfExecution = dateOfExecution;
        this.performingUserId = performingUserId;
        this.description = description;
    }

    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }

    public String getFromIban() {
        return fromIban;
    }
    public void setFromIban(String fromIban) {
        this.fromIban = fromIban;
    }

    public String getToIban() {
        return toIban;
    }
    public void setToIban(String toIban) {
        this.toIban = toIban;
    }

    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTypeOfTransaction() {
        return typeOfTransaction;
    }
    public void setTypeOfTransaction(String typeOfTransaction) {
        this.typeOfTransaction = typeOfTransaction;
    }

    public LocalDateTime getDateOfExecution() {
        return dateOfExecution;
    }
    public void setDateOfExecution(LocalDateTime dateOfExecution) {
        this.dateOfExecution = dateOfExecution;
    }

    public UUID getPerformingUserId() {
        return performingUserId;
    }
    public void setPerformingUserId(UUID performingUserId) {
        this.performingUserId = performingUserId;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
