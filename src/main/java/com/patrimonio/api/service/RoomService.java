package com.patrimonio.api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.patrimonio.api.dto.RoomResponseDTO;
import com.patrimonio.api.model.Room;
import com.patrimonio.api.model.User;
import com.patrimonio.api.repository.RoomRepository;
import com.patrimonio.api.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class RoomService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final AssignmentResponsibilityService assignmentService;

    @Transactional(readOnly = true)
    public List<RoomResponseDTO> listAll(String email, boolean administrador){
        
        if (administrador) {
            return roomRepository.findAll().stream().map(RoomResponseDTO::fromEntity).toList();
        }

        User usuario = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Usuário autenticado não encontrado."));

        List<Room> salas = assignmentService.buscarSalasDoUsuario(usuario.getId());

        return salas.stream().map(RoomResponseDTO::fromEntity).toList();
    }
}
