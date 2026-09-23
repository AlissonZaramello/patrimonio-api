package com.patrimonio.api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.patrimonio.api.model.Room;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor 
public class RoomResponseDTO {

    private UUID id;
    private String nomeBloco;
    private String nomeResponsavel;
    private String codigo;
    private String nome;
    private LocalDateTime criadoEm;

    public static RoomResponseDTO fromEntity(Room room){
        return new RoomResponseDTO(
            room.getId(),
            room.getBloco().getNome(),
            room.getUser().getNome(),
            room.getCodigo(),
            room.getNome(),
            room.getCriadoEm()
        );
    }    
}
