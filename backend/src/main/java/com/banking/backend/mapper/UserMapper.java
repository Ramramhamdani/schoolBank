package com.banking.backend.mapper;

import com.banking.backend.dto.RegisterDTO;
import com.banking.backend.dto.UserDTO;
import com.banking.backend.model.User;
import com.banking.backend.model.UserType;

// This class converts between User entity and UserDTO (Data Transfer Object)
public class UserMapper {

    // Converts a User entity to a UserDTO for API responses
    public static UserDTO toDTO(User user) {
        if (user == null) return null;
        return new UserDTO(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail()
        );
    }

    // Converts a UserDTO to a User entity for saving to database
    public static User toEntity(UserDTO dto) {
        if (dto == null) return null;
        User user = new User();
        user.setId(dto.getId());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        // set other fields if needed
        return user;
    }

    // NEW: Convert RegisterDTO → User entity (for registration)
    public static User toEntity(RegisterDTO dto) {
        if (dto == null) return null;
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());       // map password

        // Set default values for fields not in RegisterDTO
        user.setRole(UserType.CUSTOMER);           // default role
        user.setActive(true);
        user.setDayLimit(0f);
        user.setTransactionLimit(0f);
        // bsn and dateOfBirth left null for now
        return user;
    }
}