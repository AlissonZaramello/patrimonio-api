package com.patrimonio.api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.patrimonio.api.model.User;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponseDTO {

    private UUID id;
    private String nome;
    private String email;
    private String role;
    private boolean ativo;
    private LocalDateTime criadoEm;

    public static UserResponseDTO fromEntity(User user){
        return new UserResponseDTO(
            user.getId(),
            user.getNome(),
            user.getEmail(),
            user.getRole(),
            user.getAtivo(),
            user.getCriadoEm()
        );
    }
}
