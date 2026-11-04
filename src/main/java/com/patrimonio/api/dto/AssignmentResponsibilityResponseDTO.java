package com.patrimonio.api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.patrimonio.api.model.AssignmentResponsibility;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter  
public class AssignmentResponsibilityResponseDTO {

    private UUID id;
    private UUID usuarioId;
    private String nomeUsuario;
    private UUID blocoId;
    private String nomeBloco;
    private UUID salaId;
    private String nomeSala;
    private Boolean ativo;
    private LocalDateTime criadoEm;

    public static AssignmentResponsibilityResponseDTO fromEntity(AssignmentResponsibility assignment) {

        AssignmentResponsibilityResponseDTO dto = new AssignmentResponsibilityResponseDTO();

        dto.setId(assignment.getId());
        dto.setUsuarioId(assignment.getUsuario().getId());
        dto.setNomeUsuario(assignment.getUsuario().getNome());
        dto.setAtivo(assignment.getAtivo());
        dto.setCriadoEm(assignment.getCriadoEm());

        if (assignment.getBloco() != null) {
            dto.setBlocoId(assignment.getBloco().getId());
            dto.setNomeBloco(assignment.getBloco().getNome());
        }

        if (assignment.getSala() != null) {
            dto.setSalaId(assignment.getSala().getId());
            dto.setNomeSala(assignment.getSala().getNome());
        }

        return dto;
    }
}
