package com.banking.backend.dto;

import java.util.UUID;

public class LoginResponseDTO {
    private UUID userId;
    private String jwtToken;

    public LoginResponseDTO(UUID userId, String jwtToken) {
        this.userId = userId;
        this.jwtToken = jwtToken;
    }

    // Getters and setters
    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    // equals, hashCode, toString (optional but recommended)
}