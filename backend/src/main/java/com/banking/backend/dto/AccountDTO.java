package com.banking.backend.dto;

import java.time.LocalDate;
import java.util.UUID;

public class AccountDTO {
    private UUID id;
    private String iban;
    private double balance;
    private String typeOfAccount;
    private UUID userId;
    private LocalDate dateOfOpening;
    private double absoluteLimit;
    private boolean active;

    public AccountDTO() {}

    public AccountDTO(UUID id, String iban, double balance, String typeOfAccount,
                      UUID userId, LocalDate dateOfOpening, double absoluteLimit, boolean active) {
        this.id = id;
        this.iban = iban;
        this.balance = balance;
        this.typeOfAccount = typeOfAccount;
        this.userId = userId;
        this.dateOfOpening = dateOfOpening;
        this.absoluteLimit = absoluteLimit;
        this.active = active;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getIban() { return iban; }
    public void setIban(String iban) { this.iban = iban; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    public String getTypeOfAccount() { return typeOfAccount; }
    public void setTypeOfAccount(String typeOfAccount) { this.typeOfAccount = typeOfAccount; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public LocalDate getDateOfOpening() { return dateOfOpening; }
    public void setDateOfOpening(LocalDate dateOfOpening) { this.dateOfOpening = dateOfOpening; }

    public double getAbsoluteLimit() { return absoluteLimit; }
    public void setAbsoluteLimit(double absoluteLimit) { this.absoluteLimit = absoluteLimit; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}