package com.bank.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
@Data
public class Account {
    //id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //accountNumber
    @Column(nullable = false, unique = true)
    private String accountNumber;

    //accountType
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    //balance
    @Column(nullable = false)
    private BigDecimal balance;

    //status
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(nullable = false)
    private String ifscCode;

    @Column(nullable = false)
    private String branchAddress;

    //userId
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    //createdAt
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum AccountType {
        SAVINGS, CURRENT

    }

    public enum Status {
        ACTIVE, FROZEN
    }
}
