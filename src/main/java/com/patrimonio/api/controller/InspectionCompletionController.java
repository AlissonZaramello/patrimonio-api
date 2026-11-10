package com.patrimonio.api.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.patrimonio.api.service.InspectionCompletionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/conferencias")
@RequiredArgsConstructor
public class InspectionCompletionController {

    private final InspectionCompletionService finalizacaoService;

    @PatchMapping("/{id}/finalizar")
    public ResponseEntity<InspectionCompletionService.CompletionResponse>finalizar(@PathVariable UUID id, Authentication autenticacao) {

        return ResponseEntity.ok(finalizacaoService.finalizar(id, autenticacao.getName()));
    }    
}
