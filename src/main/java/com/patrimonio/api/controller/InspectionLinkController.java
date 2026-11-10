
package com.patrimonio.api.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.patrimonio.api.service.InspectionLinkService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/conferencias")
@RequiredArgsConstructor
public class InspectionLinkController {

    private final InspectionLinkService vinculacaoService;

    public record LinkRequest( @NotBlank String codigo) {}

    @PostMapping("/{id}/codigo-vinculacao")
    public ResponseEntity<InspectionLinkService.GeneratedCode> gerarCodigo(@PathVariable UUID id, Authentication autenticacao) {

        return ResponseEntity.ok(vinculacaoService.gerarCodigo(id, autenticacao.getName()));
    }

    @PostMapping("/vincular")
    public ResponseEntity<InspectionLinkService.LinkedInspection> vincular(@Valid @RequestBody LinkRequest requisicao, Authentication autenticacao) {

        return ResponseEntity.ok(vinculacaoService.vincular(requisicao.codigo(), autenticacao.getName()));
    }
}
