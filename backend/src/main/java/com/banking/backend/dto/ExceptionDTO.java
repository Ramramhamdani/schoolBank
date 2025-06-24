package com.banking.backend.dto;

public class ExceptionDTO {
    private String message;

    public ExceptionDTO() {}

    public ExceptionDTO(String message) {
        this.message = message;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}