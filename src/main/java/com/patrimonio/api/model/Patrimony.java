package com.patrimonio.api.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "patrimonio")
@Data
@NoArgsConstructor
@AllArgsConstructor 
public class Patrimony {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Column(nullable = false)
    private String numeroTombo;

    @Column
    private String descricao;

    @Column(nullable = false)
    private Long valorAquisicao;

    @Column(nullable = false)
    private LocalDateTime dataAquisicao;

    @Column(nullable = false)
    private String estadoConservacao;

    @Column
    private String fotoUrl;

    @ManyToOne
    @JoinColumn(name = "sala_id", nullable = false)
    private Room room;

    @Column(nullable = false)
    private UUID tagNfcId;

    @Column
    private Boolean ativo;

    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    @Column
    private LocalDateTime atualizadoEm;
}
