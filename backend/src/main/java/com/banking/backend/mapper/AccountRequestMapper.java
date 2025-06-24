package com.banking.backend.mapper;

import com.banking.backend.dto.AccountRequestDTO;
import com.banking.backend.model.AccountRequest;

// Converts between AccountRequest entity and AccountRequestDTO
public class AccountRequestMapper {

    // Converts AccountRequest entity to AccountRequestDTO
    public static AccountRequestDTO toDTO(AccountRequest request) {
        if (request == null) return null;
        return new AccountRequestDTO(
            request.getId(),
            request.getRequestedAccountType(),
            request.getCustomerEmail()
        );
    }

    // Converts AccountRequestDTO to AccountRequest entity
    public static AccountRequest toEntity(AccountRequestDTO dto) {
        if (dto == null) return null;
        AccountRequest request = new AccountRequest();
        request.setId(dto.getRequestId());
        request.setRequestedAccountType(dto.getRequestedAccountType());
        request.setCustomerEmail(dto.getCustomerEmail());
        return request;
    }
}