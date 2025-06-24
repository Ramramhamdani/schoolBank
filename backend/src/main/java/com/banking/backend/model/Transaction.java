package com.banking.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "from_iban", nullable = false)
    private Account fromAccount;

    @ManyToOne
    @JoinColumn(name = "to_iban", nullable = false)
    private Account toAccount;

    @Column(nullable = false)
    private double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType typeOfTransaction;

    @Column(nullable = false)
    private LocalDateTime dateOfExecution;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User performingUser;

    @Column
    private String description;
}