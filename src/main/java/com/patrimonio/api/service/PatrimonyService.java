package com.patrimonio.api.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.patrimonio.api.dto.PatrimonyResponseDTO;
import com.patrimonio.api.model.Room;
import com.patrimonio.api.model.User;
import com.patrimonio.api.repository.PatrimonyRepository;
import com.patrimonio.api.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class PatrimonyService {

    private final PatrimonyRepository patrimonyRepository;
    private final UserRepository userRepository;
    private final AssignmentResponsibilityService assignmentService;

    @Transactional(readOnly = true) 
    public List<PatrimonyResponseDTO> listAll(String email, boolean administrador){

        // Administrador possui acesso geral
        if (administrador){
            return patrimonyRepository.findAll()
                .stream()
                .map(PatrimonyResponseDTO::fromEntity)
                .toList();
        }

        // Localiza o usuário autenticado pelo e-mail do token
        User usuario = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Usuário autenticado não encontrado."));

        // Busca as salas sob responsabilidade do funcionário
        List<Room> salas = assignmentService.buscarSalasDoUsuario(usuario.getId());

        if (salas.isEmpty()) {
            return List.of();
        }
        
        List<UUID> salaIds = salas.stream().map(Room::getId).toList();

        // Busca somente os patrimônios dessas salas
        return patrimonyRepository.findByRoom_IdIn(salaIds).stream().map(PatrimonyResponseDTO::fromEntity).toList();
    }
}
