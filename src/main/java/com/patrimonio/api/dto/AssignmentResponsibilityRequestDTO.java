package com.patrimonio.api.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter  
public class AssignmentResponsibilityRequestDTO {

    @NotNull(message = "O funcionario é obrigatório.") 
    private UUID usuarioId;

    private UUID blocoId;

    private UUID salaId;
}
