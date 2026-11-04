package com.patrimonio.api.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "atribuicao_responsabilidade")
@Getter
@Setter
@NoArgsConstructor  
public class AssignmentResponsibility {

    @Id 
    private UUID id = UUID.randomUUID();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false) 
    private User usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bloco_id")
    private Block bloco;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sala_id")
    private Room sala;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criado_por", nullable = false)
    private User criadoPor;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(nullable = false)
    private Boolean ativo = true;

    @PrePersist 
    protected void prePersist() {
        if (criadoEm == null) {
            criadoEm = LocalDateTime.now();
        }

        if (ativo == null) {
            ativo = true;
        }
    }
}
