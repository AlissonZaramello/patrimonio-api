package com.patrimonio.api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.validation.Valid;

import com.patrimonio.api.dto.BlockResponseDTO;
import com.patrimonio.api.service.BlockService;
import com.patrimonio.api.dto.BlockRequestDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/blocos")
@RequiredArgsConstructor 
public class BlockController {

    private final BlockService blockService;

    @GetMapping
    public ResponseEntity<List<BlockResponseDTO>> listAll(Authentication authentication){
        
        boolean administrador = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN") || authority.getAuthority().equals("ROLE_ADMINISTRADOR"));

        return ResponseEntity.ok(blockService.listAll(authentication.getName(), administrador));
                
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BlockResponseDTO criar(@Valid @RequestBody BlockRequestDTO dados, Authentication autenticacao) {

        validarAdministrador(autenticacao);

        return blockService.criar(dados);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BlockResponseDTO> atualizar(@PathVariable UUID id, @Valid @RequestBody BlockRequestDTO dados, Authentication autenticacao) {

        validarAdministrador(autenticacao);

        return ResponseEntity.ok(blockService.atualizar(id, dados));
        
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<BlockResponseDTO> desativar(@PathVariable UUID id, Authentication autenticacao){
        
        validarAdministrador(autenticacao);

        return ResponseEntity.ok(blockService.alterarSituacao(id, false));
    }

    @PatchMapping("/{id}/reativar")
    public ResponseEntity<BlockResponseDTO> reativar(@PathVariable UUID id, Authentication autenticacao){

        validarAdministrador(autenticacao);

        return ResponseEntity.ok(blockService.alterarSituacao(id, true));
    }

    private void validarAdministrador(Authentication autenticacao) {

        boolean administrador = autenticacao.getAuthorities().stream()
                .anyMatch(autoridade -> "ROLE_ADMIN".equals(autoridade.getAuthority()) || "ROLE_ADMINISTRADOR".equals(autoridade.getAuthority()));

        if (!administrador) {

            throw new AccessDeniedException("Somente administradores podem gerenciar blocos");
        }
    }

}
