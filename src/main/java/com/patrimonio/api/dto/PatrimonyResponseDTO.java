package com.patrimonio.api.dto;

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
    private Long valorAquisicao;
    private LocalDateTime dataAquisicao;
    private String estadoConservacao;
    private String fotoUrl;
    private String nomeSala;
    private String nomeBloco;
    private String nomeResponsavelSala;
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
            patrimony.getRoom().getUser().getNome(),
            patrimony.getTagNfcId(),
            patrimony.getAtivo(),
            patrimony.getCriadoEm(),
            patrimony.getAtualizadoEm()
        );
    }
}
