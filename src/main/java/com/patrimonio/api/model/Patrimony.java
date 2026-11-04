package com.patrimonio.api.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
    private UUID id = UUID.randomUUID();

    @Column(name = "numero_tombo", nullable = false)
    private String numeroTombo;

    @Column
    private String descricao;

    @Column(name = "valor_aquisicao", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorAquisicao;

    @Column(name = "data_aquisicao", nullable = false)
    private LocalDate dataAquisicao;

    @Column(name = "estado_conservacao", nullable = false)
    private String estadoConservacao;

    @Column(name = "foto_url")
    private String fotoUrl;

    @ManyToOne
    @JoinColumn(name = "sala_id", nullable = false)
    private Room room;

    @ManyToOne
    @JoinColumn(name = "tag_nfc_id", unique = true) 
    private TagNfc tagNfc;

    @Column
    private Boolean ativo;

    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;
}
