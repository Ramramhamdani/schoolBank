package com.banking.backend.dto;

public class CustomerIbanDTO {
    private String iban;

    public CustomerIbanDTO() {}

    public CustomerIbanDTO(String iban) {
        this.iban = iban;
    }

    public String getIban() { return iban; }
    public void setIban(String iban) { this.iban = iban; }
}