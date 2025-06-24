package com.banking.backend.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data Transfer Object for login response containing JWT token.
 */
public class JwtResponseDTO {
    private String token;

    public JwtResponseDTO() {
        this.token = token;
    }
    public JwtResponseDTO(String token) {
        this.token = token;
    }
    public String getToken() {
        return token;
    }
}