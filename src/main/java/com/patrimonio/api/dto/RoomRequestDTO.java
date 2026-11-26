package com.patrimonio.api.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RoomRequestDTO( 

    @NotNull(message = "o bloco é obrigatório.")
    UUID blocoId,

    @NotBlank(message = "O código da sala é obrigatório.")
    String codigo,

    @NotBlank(message = "O nome da sala é obrigatório")
    String nome
){}
