package com.patrimonio.api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.patrimonio.api.dto.BlockResponseDTO;
import com.patrimonio.api.model.User;
import com.patrimonio.api.repository.BlockRepository;
import com.patrimonio.api.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class BlockService {

    private final BlockRepository blockRepository;
    private final UserRepository userRepository;
    private final AssignmentResponsibilityService assignmentService;    

    @Transactional(readOnly = true) 
    public List<BlockResponseDTO> listAll(String email, boolean administrador){
        
        if (administrador) {
            return blockRepository.findAll().stream().map(BlockResponseDTO::fromEntity).toList();
        }

        User usuario = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Usuario autenticado não encontrado."));

        return assignmentService.buscarBlocosDoUsuario(usuario.getId()).stream().map(BlockResponseDTO::fromEntity).toList();

    }
}
