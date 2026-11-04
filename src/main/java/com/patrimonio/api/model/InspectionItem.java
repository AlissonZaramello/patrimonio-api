package com.patrimonio.api.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "item_conferencia",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_item_conferencia_patrimonio",
            columnNames = {"conferencia_id", "patrimonio_id"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
public class InspectionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conferencia_id", nullable = false)
    private Inspection conferencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patrimonio_id", nullable = false)
    private Patrimony patrimonio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sala_esperada_id", nullable = false)
    private Room salaEsperada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sala_lida_id")
    private Room salaLida;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "PENDENTE";

    @Column(name = "lido_em")
    private LocalDateTime lidoEm;

    @Column(name = "observacao", length = 500)
    private String observacao;

    @PrePersist
    protected void onCreate() {
        if (status == null) {
            status = "PENDENTE";
        }
    }
}