package com.patrimonio.api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.patrimonio.api.service.InspectionQueryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/conferencias")
@RequiredArgsConstructor
public class InspectionQueryController {

    private final InspectionQueryService consultaService;

    @GetMapping
    public ResponseEntity<List<InspectionQueryService.InspectionResponse>>listar(Authentication autenticacao) {

        return ResponseEntity.ok(consultaService.listar(autenticacao));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InspectionQueryService.InspectionResponse>buscarPorId(@PathVariable UUID id, Authentication autenticacao) {

        return ResponseEntity.ok(consultaService.buscarPorId(id, autenticacao));
    }

    @GetMapping("/{id}/itens")
    public ResponseEntity<List<InspectionQueryService.InspectionItemResponse>>listarItens(@PathVariable UUID id, Authentication autenticacao) {

        return ResponseEntity.ok(consultaService.listarItens(id, autenticacao));
    }
}
