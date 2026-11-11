package com.patrimonio.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PatrimonyRequestDTO(
        @NotBlank String numeroTombo,
        String descricao,
        @NotNull @DecimalMin("0.00") BigDecimal valorAquisicao,
        @NotNull LocalDate dataAquisicao,
        @NotBlank String estadoConservacao,
        String fotoUrl,
        @NotNull UUID salaId,
        UUID tagNfcId,
        Boolean ativo
) {}
