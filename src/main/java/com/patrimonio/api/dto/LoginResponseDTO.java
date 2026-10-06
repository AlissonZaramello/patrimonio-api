package com.patrimonio.api.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDTO {

    private String token;
    private UUID id;
    private String nome;
    private String email;
    private String role;
}
