package com.banking.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email")) //email is unique no duplicate users
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String bsn;
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private UserType role;

    private float dayLimit;
    private float transactionLimit;
    private boolean active;
}