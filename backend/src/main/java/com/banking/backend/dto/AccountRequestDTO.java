package com.banking.backend.dto;

public class AccountRequestDTO {
    private Long requestId;
    private String requestedAccountType; // You can make this an enum later
    private String customerEmail;

    public AccountRequestDTO() {}

    public AccountRequestDTO(Long requestId, String requestedAccountType, String customerEmail) {
        this.requestId = requestId;
        this.requestedAccountType = requestedAccountType;
        this.customerEmail = customerEmail;
    }

    public Long getRequestId() { return requestId; }
    public void setRequestId(Long requestId) { this.requestId = requestId; }

    public String getRequestedAccountType() { return requestedAccountType; }
    public void setRequestedAccountType(String requestedAccountType) { this.requestedAccountType = requestedAccountType; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
}