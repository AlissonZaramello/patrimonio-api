
package com.patrimonio.api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;

import com.patrimonio.api.dto.InspectionCycleRequestDTO;
import com.patrimonio.api.dto.InspectionCycleResponseDTO;
import com.patrimonio.api.model.InspectionCycle;
import com.patrimonio.api.service.InspectionCycleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ciclos-conferencia")
@RequiredArgsConstructor
public class InspectionCycleController {

    private final InspectionCycleService cicloService;

    @GetMapping
    public ResponseEntity<List<InspectionCycleResponseDTO>> listar(Authentication authentication) {

        verificarAdministrador(authentication);

        List<InspectionCycleResponseDTO> resposta = cicloService.listarTodos().stream().map(InspectionCycleResponseDTO::fromEntity).toList();

        return ResponseEntity.ok(resposta);

    }

    @GetMapping("/{id}")
    public ResponseEntity<InspectionCycleResponseDTO> buscarPorId(@PathVariable UUID id, Authentication authentication) {

        verificarAdministrador(authentication);

        InspectionCycle ciclo = cicloService.buscarPorId(id);

        return ResponseEntity.ok(InspectionCycleResponseDTO.fromEntity(ciclo));
    }

    @PostMapping
    public ResponseEntity<InspectionCycleResponseDTO> criar(@Valid @RequestBody InspectionCycleRequestDTO dto, Authentication authentication) {

        verificarAdministrador(authentication);

        InspectionCycle ciclo = cicloService.criar(dto, authentication.getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(InspectionCycleResponseDTO.fromEntity(ciclo));
    }

    private void verificarAdministrador(Authentication authentication) {

        boolean administrador = authentication.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN") || authority.getAuthority().equals("ROLE_ADMINISTRADOR"));

        if (!administrador) {

            throw new AccessDeniedException("Apenas administradores podem gerenciar ciclos de conferência.");
        }
        
    }

    @PutMapping("/{id}")
    public ResponseEntity<InspectionCycleResponseDTO> atualizar(@PathVariable UUID id, @Valid @RequestBody InspectionCycleRequestDTO dto, Authentication authentication) {

        verificarAdministrador(authentication);

        InspectionCycle ciclo = cicloService.atualizar(id, dto);

        return ResponseEntity.ok(InspectionCycleResponseDTO.fromEntity(ciclo));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<InspectionCycleResponseDTO> cancelar(@PathVariable UUID id, Authentication authentication) {

        verificarAdministrador(authentication);

        InspectionCycle ciclo = cicloService.cancelar(id);

        return ResponseEntity.ok(InspectionCycleResponseDTO.fromEntity(ciclo));
    }

}
