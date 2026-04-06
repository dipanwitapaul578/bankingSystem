package com.bank.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
public class Transaction {

    //id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    //from_account_id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "from_account_id", nullable = true)
    private Account fromAccount;

    //to_account_id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "to_account_id")
    private Account toAccount;

    //amount
    @Column(nullable = false)
    private BigDecimal amount;

    //type
    @Enumerated(EnumType.STRING)
    private Type type;

    //Category
    @Column(nullable = true)
    private String category;

    //description
    @Column(nullable = true)
    private String description;

    //created_at
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();
    }

    public enum Type{
        DEPOSIT, WITHDRAWAL, TRANSFER
    }

}
