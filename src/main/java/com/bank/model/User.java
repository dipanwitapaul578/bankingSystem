package com.bank.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Role role;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;


    public enum Status {
        ACTIVE, INACTIVE
    }

    @PrePersist
    protected void onCreate() {

        createdAt = LocalDateTime.now();
        if (status == null) status = Status.ACTIVE;
    }

    public enum Role {
        ADMIN, ANALYST, VIEWER
    }
}