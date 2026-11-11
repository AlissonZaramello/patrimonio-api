package com.patrimonio.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;

import java.util.List;

import com.patrimonio.api.dto.PatrimonyResponseDTO;
import com.patrimonio.api.service.PatrimonyService;
import com.patrimonio.api.dto.PatrimonyRequestDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/patrimonios")
@RequiredArgsConstructor  
public class PatrimonyController {

    private final PatrimonyService patrimonyService;

    @GetMapping
    public ResponseEntity<List<PatrimonyResponseDTO>> listAll(Authentication authentication){
        
        boolean administrador = authentication.getAuthorities()
                .stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN") || authority.getAuthority().equals("ROLE_ADMINISTRADOR"));

        return ResponseEntity.ok(patrimonyService.listAll(authentication.getName(), administrador));
                
    }

    @PostMapping
    public ResponseEntity<PatrimonyResponseDTO> criar(@Valid @RequestBody PatrimonyRequestDTO requisicao, Authentication autenticacao) {

        boolean administrador = autenticacao.getAuthorities().stream()
                .anyMatch(autoridade -> "ROLE_ADMIN".equals(autoridade.getAuthority()) || "ROLE_ADMINISTRADOR".equals(autoridade.getAuthority()));

        if (!administrador) {

            throw new AccessDeniedException("Somente administradores podem cadastrar patrimônios.");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(patrimonyService.criar(requisicao));
    }
}
