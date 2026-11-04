
package com.patrimonio.api.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InspectionCycleRequestDTO {

    @NotBlank(message = "O nome do ciclo é obrigatório.")
    private String nome;

    @NotNull(message = "O mês de referência é obrigatório.")
    @Min(value = 1, message = "O mês deve estar entre 1 e 12.")
    @Max(value = 12, message = "O mês deve estar entre 1 e 12.")
    private Integer mesReferencia;

    @NotNull(message = "O ano de referência é obrigatório.")
    @Min(value = 2000, message = "O ano de referência é inválido.")
    private Integer anoReferencia;

    @NotNull(message = "A data inicial é obrigatória.")
    private LocalDate dataInicio;

    @NotNull(message = "A data final é obrigatória.")
    private LocalDate dataFim;
}
