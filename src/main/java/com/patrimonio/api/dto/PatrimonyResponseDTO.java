package com.patrimonio.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.patrimonio.api.model.Patrimony;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PatrimonyResponseDTO {

    private UUID id;
    private String numeroTombo;
    private String descricao;
    private BigDecimal valorAquisicao;
    private LocalDate dataAquisicao;
    private String estadoConservacao;
    private String fotoUrl;
    private String nomeSala;
    private String nomeBloco;
    private UUID tagNfcId;
    private Boolean ativo;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    public static PatrimonyResponseDTO fromEntity(Patrimony patrimony){
        return new PatrimonyResponseDTO(
            patrimony.getId(),
            patrimony.getNumeroTombo(),
            patrimony.getDescricao(),
            patrimony.getValorAquisicao(),
            patrimony.getDataAquisicao(),
            patrimony.getEstadoConservacao(),
            patrimony.getFotoUrl(),
            patrimony.getRoom().getNome(),
            patrimony.getRoom().getBloco().getNome(),
            patrimony.getTagNfc() != null ? patrimony.getTagNfc().getId() : null,
            patrimony.getAtivo(),
            patrimony.getCriadoEm(),
            patrimony.getAtualizadoEm()
        );
    }
}
