package com.banking.backend.cucumber.helpers;

import com.banking.backend.dto.*;

import java.util.UUID;

public class TestDataBuilder {

    public static RegisterDTO createRegisterDTO(String firstName, String lastName, String email, String password) {
        return new RegisterDTO(firstName, lastName, email, password);
    }

    public static RegisterDTO createValidRegisterDTO() {
        return createRegisterDTO("John", "Doe", "john.doe@test.com", "password123");
    }

    public static LoginDTO createLoginDTO(String email, String password) {
        return new LoginDTO(email, password);
    }

    public static AccountRequestDTO createAccountRequestDTO(String accountType, String customerEmail) {
        AccountRequestDTO dto = new AccountRequestDTO();
        dto.setRequestedAccountType(accountType);
        dto.setCustomerEmail(customerEmail);
        return dto;
    }

    public static TransactionDTO createTransactionDTO(String fromIban, String toIban, double amount, UUID performingUserId) {
        return createTransactionDTO(fromIban, toIban, amount, performingUserId, "Test transaction");
    }

    public static TransactionDTO createTransactionDTO(String fromIban, String toIban, double amount, UUID performingUserId, String description) {
        TransactionDTO dto = new TransactionDTO();
        dto.setFromIban(fromIban);
        dto.setToIban(toIban);
        dto.setAmount(amount);
        dto.setPerformingUserId(performingUserId);
        dto.setDescription(description);
        dto.setTypeOfTransaction("TRANSFER");
        return dto;
    }

    public static String generateTestIban(String suffix) {
        return "NL" + String.format("%02d", suffix.hashCode() % 100) + "BANK" + String.format("%010d", Math.abs(suffix.hashCode()));
    }
}
