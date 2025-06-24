package com.banking.backend.mapper;

import com.banking.backend.dto.AccountDTO;
import com.banking.backend.model.Account;
import com.banking.backend.model.AccountType;

public class AccountMapper {

    // Converts Account entity → AccountDTO
    public static AccountDTO toDTO(Account account) {
        if (account == null) return null;
        return new AccountDTO(
            account.getId(),
            account.getIban(),
            account.getBalance(),
            account.getTypeOfAccount() != null ? account.getTypeOfAccount().name() : null,
            // New fields below:
            account.getUser() != null ? account.getUser().getId() : null,
            account.getDateOfOpening(),
            account.getAbsoluteLimit(),
            account.isActive()
        );
    }

    // Converts AccountDTO → Account entity
    public static Account toEntity(AccountDTO dto) {
        if (dto == null) return null;
        Account account = new Account();
        account.setId(dto.getId());
        account.setIban(dto.getIban());
        account.setBalance(dto.getBalance());
        if (dto.getTypeOfAccount() != null) {
            account.setTypeOfAccount(AccountType.valueOf(dto.getTypeOfAccount()));
        }
        // You might set defaults or leave the following for the service layer:
        // account.setDateOfOpening(dto.getDateOfOpening());
        // account.setAbsoluteLimit(dto.getAbsoluteLimit());
        // account.setActive(dto.isActive());
        // User reference should be looked up in the service, not here:
        // account.setUser(userService.findById(dto.getUserId()));
        return account;
    }
}
