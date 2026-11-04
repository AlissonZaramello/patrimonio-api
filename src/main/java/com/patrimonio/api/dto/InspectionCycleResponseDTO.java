
package com.patrimonio.api.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.patrimonio.api.model.InspectionCycle;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InspectionCycleResponseDTO {

    private UUID id;
    private String nome;
    private Integer mesReferencia;
    private Integer anoReferencia;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private String status;
    private UUID criadoPorId;
    private String nomeCriador;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    public static InspectionCycleResponseDTO fromEntity(InspectionCycle ciclo) {

        InspectionCycleResponseDTO dto = new InspectionCycleResponseDTO();

        dto.setId(ciclo.getId());
        dto.setNome(ciclo.getNome());
        dto.setMesReferencia(ciclo.getMesReferencia());
        dto.setAnoReferencia(ciclo.getAnoReferencia());
        dto.setDataInicio(ciclo.getDataInicio());
        dto.setDataFim(ciclo.getDataFim());
        dto.setStatus(ciclo.getStatus());
        dto.setCriadoEm(ciclo.getCriadoEm());
        dto.setAtualizadoEm(ciclo.getAtualizadoEm());

        if (ciclo.getCriadoPor() != null) {
            dto.setCriadoPorId(ciclo.getCriadoPor().getId());
            dto.setNomeCriador(ciclo.getCriadoPor().getNome());
        }

        return dto;
    }
}
