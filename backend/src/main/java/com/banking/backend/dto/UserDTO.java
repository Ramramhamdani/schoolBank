package com.banking.backend.dto;
import com.banking.backend.model.User;
import com.banking.backend.model.UserType;

import java.util.UUID;

public class UserDTO {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;

    public UserDTO() {}

    public UserDTO(UUID id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public static User toEntity(RegisterDTO dto) {
        if (dto == null) return null;
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());     // map password

        // Set default values for fields not in RegisterDTO
        user.setRole(UserType.CUSTOMER);         // default user role
        user.setActive(true);
        user.setDayLimit(0f);
        user.setTransactionLimit(0f);
    // Leave bsn and dateOfBirth as null for now

    return user;
    }

    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}