package com.patrimonio.api.controller;

import com.patrimonio.api.dto.UserResponseDTO;
import com.patrimonio.api.service.UserService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.AccessDeniedException;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> listAll(
            Authentication autenticacao) {

        boolean administrador = autenticacao.getAuthorities().stream()
                .anyMatch(autoridade -> "ROLE_ADMIN".equals(autoridade.getAuthority()) || "ROLE_ADMINISTRADOR".equals(autoridade.getAuthority()));

        if (!administrador) {
            
            throw new AccessDeniedException("Somente administradores podem consultar os usuários.");
        }

        return ResponseEntity.ok(userService.listAll());
    }
}
