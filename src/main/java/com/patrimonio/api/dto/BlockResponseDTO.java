package com.patrimonio.api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.patrimonio.api.model.Block;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data 
@AllArgsConstructor 
public class BlockResponseDTO {

    private UUID id;
    private String codigo;
    private String nome;
    private String descricao;
    private Boolean ativo;
    private LocalDateTime criadoEm;

    public static BlockResponseDTO fromEntity(Block block){
        return new BlockResponseDTO(
            block.getId(),
            block.getCodigo(),
            block.getNome(),
            block.getDescricao(),
            block.getAtivo(),
            block.getCriadoEm()
        );
    }
}
