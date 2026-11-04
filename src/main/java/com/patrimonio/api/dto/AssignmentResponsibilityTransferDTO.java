package com.patrimonio.api.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignmentResponsibilityTransferDTO {

    @NotNull(message = "O novo funcionário é obrigatório.")
    private UUID novoUsuarioId;
}