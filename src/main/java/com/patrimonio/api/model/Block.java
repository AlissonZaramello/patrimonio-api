package com.patrimonio.api.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity 
@Table (name = "bloco")
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
public class Block {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private UUID id;

    @Column(nullable = false) 
    private String code;

    @Column(nullable = false)
    private String name; 
    
    @Column
    private String description;

    @Column(name = "criado_em", updatable = false)
    private LocalDateTime createdOn;
}
