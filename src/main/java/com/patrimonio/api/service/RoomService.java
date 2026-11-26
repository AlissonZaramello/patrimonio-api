package com.patrimonio.api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.patrimonio.api.dto.RoomRequestDTO;
import com.patrimonio.api.dto.RoomResponseDTO;
import com.patrimonio.api.model.Block;
import com.patrimonio.api.model.Room;
import com.patrimonio.api.model.User;
import com.patrimonio.api.repository.BlockRepository;
import com.patrimonio.api.repository.RoomRepository;
import com.patrimonio.api.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class RoomService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final AssignmentResponsibilityService assignmentService;
    private final BlockRepository blockRepository;

    @Transactional(readOnly = true)
    public List<RoomResponseDTO> listAll(String email, boolean administrador){
        
        if (administrador) {

            return roomRepository.findAll().stream().map(RoomResponseDTO::fromEntity).toList();
        }

        User usuario = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Usuário autenticado não encontrado."));

        List<Room> salas = assignmentService.buscarSalasDoUsuario(usuario.getId());

        return salas.stream().map(RoomResponseDTO::fromEntity).toList();
    }

    @Transactional 
    public RoomResponseDTO criar(RoomRequestDTO dados) {

        Block bloco = blockRepository.findById(dados.blocoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bloco não encontrado."));

        if (!Boolean.TRUE.equals(bloco.getAtivo())) {

            throw new ResponseStatusException(HttpStatus.CONFLICT, "Não é possivel cadastrar salas em um bloco inativo.");
        }
        
        String codigo = dados.codigo().trim().toUpperCase();
        String nome = dados.nome().trim();

        boolean codigoEmUso = roomRepository.existsByBloco_IdAndCodigoIgnoreCase(bloco.getId(), codigo);

        if (codigoEmUso) {
            
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma sala com esse código neste bloco.");
        }

        Room sala = new Room();

        sala.setBloco(bloco);
        sala.setCodigo(codigo);
        sala.setNome(nome);

        sala.setCriadoEm(LocalDateTime.now());

        return RoomResponseDTO.fromEntity(roomRepository.save(sala));

    }

    @Transactional 
    public RoomResponseDTO atualizar(UUID id, RoomRequestDTO dados) {
        
        Room sala = roomRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sala não encontrada"));

        Block bloco = blockRepository.findById(dados.blocoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bloco não encontrado."));
                
        if (!Boolean.TRUE.equals(bloco.getAtivo())){

            throw new ResponseStatusException(HttpStatus.CONFLICT, "Não é possível vincular a sala a um bloco inativo.");
        }
        
        String codigo = dados.codigo().trim().toUpperCase();

        boolean codigoEmUso = roomRepository.existsByBloco_IdAndCodigoIgnoreCaseAndIdNot(bloco.getId(), codigo, id);

        if (codigoEmUso) {

            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe outra sala com esse código neste bloco.");
        }

        sala.setCodigo(codigo);
        sala.setNome(dados.nome().trim());

        // Alterar o bloco pode afetar atribuições de responsabilidade.
        // Por enquanto, não permitiremos essa mudança.
        if (!sala.getBloco().getId().equals(bloco.getId())) {
            
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A transferência de uma sala para outro bloco será realizada por uma operação específica.");
        }

        return RoomResponseDTO.fromEntity(roomRepository.save(sala));
    }
}
