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
    private String code;
    private String name;
    private String description;
    private LocalDateTime createdOn;

    public static BlockResponseDTO fromEntity(Block block){
        return new BlockResponseDTO(
            block.getId(),
            block.getCode(),
            block.getName(),
            block.getDescription(),
            block.getCreatedOn()
        );
    }
}
