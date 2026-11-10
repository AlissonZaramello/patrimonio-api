package com.patrimonio.api.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.patrimonio.api.service.InspectionReadingService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/conferencias")
@RequiredArgsConstructor
public class InspectionReadingController {

    private final InspectionReadingService leituraService;

    public record ReadingRequest(@NotBlank String uuidTag) {}

    @PostMapping("/{id}/leituras-nfc")
    public ResponseEntity<InspectionReadingService.ReadingResponse>registrarLeitura(@PathVariable UUID id, @Valid @RequestBody ReadingRequest requisicao, Authentication autenticacao) {

        return ResponseEntity.ok(leituraService.registrarLeitura(id, requisicao.uuidTag(), autenticacao.getName()));
    }
}
