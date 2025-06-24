package com.banking.backend.model;

import jakarta.persistence.*;

@Entity
public class AccountRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String requestedAccountType; // Or use AccountType enum
    private String customerEmail; // The Email of the user making the request

   

    public AccountRequest() {}

    public AccountRequest(String requestedAccountType, String customerEmail) {
        this.requestedAccountType = requestedAccountType;
        this.customerEmail = customerEmail;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRequestedAccountType() { return requestedAccountType; }
    public void setRequestedAccountType(String requestedAccountType) { this.requestedAccountType = requestedAccountType; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
}