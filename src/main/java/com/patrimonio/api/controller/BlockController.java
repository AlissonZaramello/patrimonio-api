package com.patrimonio.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.patrimonio.api.dto.BlockResponseDTO;
import com.patrimonio.api.service.BlockService;

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

}
