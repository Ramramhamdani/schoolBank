package com.banking.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String iban;

    @Column(nullable = false)
    private double balance;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_of_account", nullable = false)
    private AccountType typeOfAccount;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "date_of_opening")
    private LocalDate dateOfOpening;

    @Column(name = "absolute_limit")
    private double absoluteLimit;

    @Column(name = "active")
    private boolean active;

    
     @OneToMany(mappedBy = "fromAccount")
     private List<Transaction> sentTransactions;
     @OneToMany(mappedBy = "toAccount")
     private List<Transaction> receivedTransactions;

    // equals and hashCode for object comparison (optional but good)
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Account other = (Account) obj;
        return Objects.equals(id, other.id) && Objects.equals(iban, other.iban);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, iban);
    }
}