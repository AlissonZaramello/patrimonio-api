package com.patrimonio.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BlockRequestDTO(

    @NotBlank(message = "O código do bloco é obrigatório.")
    @Size(max = 50)
    String codigo,

    @NotBlank(message = "O nome do bloco é obrigatório.")
    @Size(max = 150)
    String nome,

    @Size(max = 500)
    String descricao

) {}
