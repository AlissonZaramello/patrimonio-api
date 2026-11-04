
package com.patrimonio.api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.patrimonio.api.dto.AssignmentResponsibilityRequestDTO;
import com.patrimonio.api.dto.AssignmentResponsibilityResponseDTO;
import com.patrimonio.api.service.AssignmentResponsibilityService;
import com.patrimonio.api.dto.AssignmentResponsibilityTransferDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/atribuicoes-responsabilidade")
@RequiredArgsConstructor
public class AssignmentResponsibilityController {

    private final AssignmentResponsibilityService assignmentService;

    @GetMapping
    public ResponseEntity<List<AssignmentResponsibilityResponseDTO>> listar(Authentication authentication) {

        verificarAdministrador(authentication);

        return ResponseEntity.ok(assignmentService.listarTodas());
    }

    @PostMapping
    public ResponseEntity<AssignmentResponsibilityResponseDTO> criar(@Valid @RequestBody AssignmentResponsibilityRequestDTO dto, Authentication authentication) {

        verificarAdministrador(authentication);

        AssignmentResponsibilityResponseDTO resposta = assignmentService.criar(dto, authentication.getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    private void verificarAdministrador(Authentication authentication) {

        boolean administrador = authentication.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN") || authority.getAuthority().equals("ROLE_ADMINISTRADOR"));

        if (!administrador) {
            throw new AccessDeniedException("Apenas administradores podem gerenciar atribuições.");
        }
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<AssignmentResponsibilityResponseDTO> desativar(@PathVariable UUID id, Authentication authentication) {

        verificarAdministrador(authentication);

        AssignmentResponsibilityResponseDTO resposta = assignmentService.desativar(id);

        return ResponseEntity.ok(resposta);
    }

    @PostMapping("/{id}/transferir")
    public ResponseEntity<AssignmentResponsibilityResponseDTO> transferir(@PathVariable UUID id, @Valid @RequestBody AssignmentResponsibilityTransferDTO dto, Authentication authentication) {

        verificarAdministrador(authentication);

        AssignmentResponsibilityResponseDTO resposta = assignmentService.transferir(id, dto, authentication.getName());

        return ResponseEntity.ok(resposta);
    }
    
}
