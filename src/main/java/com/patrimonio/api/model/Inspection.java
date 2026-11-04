
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
    name = "conferencia",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_conferencia_ciclo_sala",
            columnNames = {"ciclo_id", "sala_id"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
public class Inspection {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ciclo_id")
    private InspectionCycle ciclo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sala_id", nullable = false)
    private Room sala;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsavel_id", nullable = false)
    private User responsavel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operador_id")
    private User operador;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "PENDING";

    @Column(name = "iniciada_em")
    private LocalDateTime iniciadaEm;

    @Column(name = "encerrada_em")
    private LocalDateTime encerradaEm;

    @Column(name = "motivo_cancelamento")
    private String motivoCancelamento;

    @Column(name = "total_esperados", nullable = false)
    private Integer totalEsperados = 0;

    @Column(name = "total_encontrados", nullable = false)
    private Integer totalEncontrados = 0;

    @Column(name = "total_ausentes", nullable = false)
    private Integer totalAusentes = 0;

    @Column(name = "total_divergentes", nullable = false)
    private Integer totalDivergentes = 0;

    @PrePersist
    protected void onCreate() {
        if (status == null) {
            status = "PENDENTE";
        }

        if (totalEsperados == null) {
            totalEsperados = 0;
        }

        if (totalEncontrados == null) {
            totalEncontrados = 0;
        }

        if (totalAusentes == null) {
            totalAusentes = 0;
        }

        if (totalDivergentes == null) {
            totalDivergentes = 0;
        }
    }
}
