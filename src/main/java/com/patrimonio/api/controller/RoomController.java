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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.patrimonio.api.dto.RoomRequestDTO;
import com.patrimonio.api.dto.RoomResponseDTO;
import com.patrimonio.api.service.RoomService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/salas")
@RequiredArgsConstructor  
public class RoomController {

    private final RoomService roomService;

    @GetMapping 
    public ResponseEntity<List<RoomResponseDTO>> listAll(Authentication authentication){
        
        boolean administrador = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN") || authority.getAuthority().equals("ROLE_ADMINISTRADOR"));

        return ResponseEntity.ok(roomService.listAll(authentication.getName(), administrador));        

    }

    @PostMapping 
    public ResponseEntity<RoomResponseDTO> criar(@Valid @RequestBody RoomRequestDTO dados, Authentication autenticacao) {

        validarAdministrador(autenticacao);

        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.criar(dados));        
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomResponseDTO> atualizar(@PathVariable UUID id, @Valid @RequestBody RoomRequestDTO dados, Authentication autenticacao) {

        validarAdministrador(autenticacao);

        return ResponseEntity.ok(roomService.atualizar(id, dados));
    }

    private void validarAdministrador(Authentication autenticacao) {

        boolean administrador = autenticacao.getAuthorities().stream()
                .anyMatch(autoridade -> "ROLE_ADMIN".equals(autoridade.getAuthority()) || "ROLE_ADMINISTRADOR".equals(autoridade.getAuthority()));

        if (!administrador) {

            throw new AccessDeniedException("Somente administradores podem gerenciar salas");
        }
    }
}
