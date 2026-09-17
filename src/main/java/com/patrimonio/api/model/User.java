package com.patrimonio.api.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column
    private String passwordHash;

    @Column
    private String role;

    @Column
    private boolean active;

    @Column
    private UUID createdBy;

    @Column(name = "created_on", updatable = false)
    private LocalDateTime createdOn;

    @Column
    private LocalDateTime updatedOn;

}
