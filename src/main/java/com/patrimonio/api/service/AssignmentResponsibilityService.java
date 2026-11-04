package com.patrimonio.api.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.patrimonio.api.model.AssignmentResponsibility;
import com.patrimonio.api.model.Block;
import com.patrimonio.api.model.Room;
import com.patrimonio.api.model.User;
import com.patrimonio.api.repository.AssignmentResponsibilityRepository;
import com.patrimonio.api.repository.BlockRepository;
import com.patrimonio.api.repository.RoomRepository;
import com.patrimonio.api.dto.AssignmentResponsibilityRequestDTO;
import com.patrimonio.api.dto.AssignmentResponsibilityResponseDTO;
import com.patrimonio.api.dto.AssignmentResponsibilityTransferDTO;
import com.patrimonio.api.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor  
public class AssignmentResponsibilityService {

    private final AssignmentResponsibilityRepository assignmentRepository;
    private final RoomRepository roomRepository;
    private final BlockRepository blockRepository;
    private final UserRepository userRepository;

    public Optional<User> buscarResponsavelDaSala(UUID salaId) {
        
        Room sala = roomRepository.findById(salaId).orElseThrow(() -> new IllegalArgumentException("Sala não encontrada: " + salaId));

        // 1. Verifica se existe uma atribuição direta para a sala
        Optional<AssignmentResponsibility> atribuicaoSala = assignmentRepository.findBySala_IdAndAtivoTrue(salaId);

        if (atribuicaoSala.isPresent()) {
            return Optional.of(atribuicaoSala.get().getUsuario());
        }

        // 2. Caso não exista, verifica a atribuição do bloco
        return assignmentRepository.findByBloco_IdAndAtivoTrue(sala.getBloco().getId()).map(AssignmentResponsibility::getUsuario);
    }

    @Transactional(readOnly = true) 
    public List<Room> buscarSalasDoUsuario(UUID usuarioId) {
        
        List<AssignmentResponsibility> atribuicoes = assignmentRepository.findByUsuario_IdAndAtivoTrue(usuarioId);

        Set<UUID> salasDiretas = new HashSet<>();
        Set<UUID> blocos = new HashSet<>();

        for (AssignmentResponsibility atribuicao : atribuicoes) {

            if (atribuicao.getSala() != null) {
                salasDiretas.add(atribuicao.getSala().getId());
            }

            if (atribuicao.getBloco() != null) {
                blocos.add(atribuicao.getBloco().getId());
            }
        }

        // Salas atribuídas diretamente ao funcionário
        Map<UUID, Room> salasEncontradas = new LinkedHashMap<>();

        if (!salasDiretas.isEmpty()) {
            roomRepository.findByIdIn(salasDiretas).forEach(sala -> salasEncontradas.put(sala.getId(), sala));

        }

        //Salas pertencentes aos blocos atribuídos ao funcionário
        if (!blocos.isEmpty()) {

            List<Room> salasDosBlocos = roomRepository.findByBloco_IdIn(blocos);

            List<UUID> idsSalasDosBlocos = salasDosBlocos.stream().map(Room::getId).toList();

            List<AssignmentResponsibility> atribuicoesDiretas = idsSalasDosBlocos.isEmpty() ? List.of() : assignmentRepository.findBySala_IdInAndAtivoTrue(idsSalasDosBlocos);

            Set<UUID> salasComResponsavelDireto = new HashSet<>();

            for (AssignmentResponsibility atribuicao : atribuicoesDiretas) {

                if (atribuicao.getSala() != null) {
                    salasComResponsavelDireto.add(atribuicao.getSala().getId());
                }
            }

            for(Room sala : salasDosBlocos) {
                //Não inclui salas que possuem responsável direto,
                //pois elas são exeções a responsabilidade do bloco.
                if (!salasComResponsavelDireto.contains(sala.getId())) {
                    salasEncontradas.put(sala.getId(), sala);
                }
            }

        }

        return new ArrayList<>(salasEncontradas.values());
    }

    @Transactional(readOnly = true)
    public List<Block> buscarBlocosDoUsuario(UUID usuarioId) {

        List<AssignmentResponsibility> atribuicoes = assignmentRepository.findByUsuario_IdAndAtivoTrue(usuarioId);

        Set<UUID> blocosIds = new HashSet<>();

        for (AssignmentResponsibility atribuicao : atribuicoes) {

            // Responsabilidade atribuida diretamente ao bloco
            if (atribuicao.getBloco() != null) {
                blocosIds.add(atribuicao.getBloco().getId());
            }

            // Responsabilidade atribuida diretamente a uma sala
            if (atribuicao.getSala() != null) {
                blocosIds.add(atribuicao.getSala().getBloco().getId());
            }

        }

        if (blocosIds.isEmpty()) {
            return List.of();
        }

        return blockRepository.findAllById(blocosIds);
    }

    @Transactional(readOnly = true)
    public List<AssignmentResponsibilityResponseDTO> listarTodas() {

        return assignmentRepository.findAll().stream().map(AssignmentResponsibilityResponseDTO::fromEntity).toList();
    }

    @Transactional
    public AssignmentResponsibilityResponseDTO criar(AssignmentResponsibilityRequestDTO dto, String emailAdministrador) {

        // Valida se exatamente um destino foi informado
        boolean possuiBloco = dto.getBlocoId() != null;
        boolean possuiSala = dto.getSalaId() != null;

        if (possuiBloco == possuiSala) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe um bloco ou uma sala, mas não ambos.");
        }

        // Busca o funcionario
        User usuario = userRepository.findById(dto.getUsuarioId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionário não encontrado."));

        if (!Boolean.TRUE.equals(usuario.getAtivo())) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não é possível atribuir responsabilidade a um usuário inativo.");
        }

        if (!"funcionario".equalsIgnoreCase(usuario.getRole())) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A atribuição deve ser feita para um funcionário.");
        }

        // Busca o administrador autenticado
        User administrador = userRepository.findByEmail(emailAdministrador).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Administrador autenticado não encontrado."));

        AssignmentResponsibility assignment = new AssignmentResponsibility();

        assignment.setUsuario(usuario);
        assignment.setCriadoPor(administrador);
        assignment.setAtivo(true);

        // Atribuição para bloco
        if (possuiBloco) {

            Block bloco = blockRepository.findById(dto.getBlocoId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bloco não encontrado."));

            if (assignmentRepository.existsByBloco_IdAndAtivoTrue(bloco.getId())) {

                throw new ResponseStatusException(HttpStatus.CONFLICT, "Este bloco já possui uma atribuição ativa.");
            }

            assignment.setBloco(bloco);
        }

        // Atribuição para sala
        if (possuiSala) {

            Room sala = roomRepository.findById(dto.getSalaId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sala não encontrada."));

            if (assignmentRepository.existsBySala_IdAndAtivoTrue(sala.getId())) {

                throw new ResponseStatusException(HttpStatus.CONFLICT, "Esta sala já possui uma atribuição ativa");
            }

            assignment.setSala(sala);
        }

        AssignmentResponsibility salva = assignmentRepository.save(assignment);

        return AssignmentResponsibilityResponseDTO.fromEntity(salva);

    }

    @Transactional
    public AssignmentResponsibilityResponseDTO desativar(UUID id) {

        AssignmentResponsibility atribuicao = assignmentRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Atribuição de responsabilidade não encontrada."));

        if (!Boolean.TRUE.equals(atribuicao.getAtivo())) {
            
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Esta atribuição já está desativada.");
        }

        atribuicao.setAtivo(false);

        AssignmentResponsibility atualizada = assignmentRepository.save(atribuicao);

        return AssignmentResponsibilityResponseDTO.fromEntity(atualizada);
    }

    @Transactional
    public AssignmentResponsibilityResponseDTO transferir(UUID atribuicaoId, AssignmentResponsibilityTransferDTO dto, String emailAdministrador) {

        // Busca a atribuição atual
        AssignmentResponsibility atual = assignmentRepository.findById(atribuicaoId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Atribuição de responsabilidade não encontrada."));

        if (!Boolean.TRUE.equals(atual.getAtivo())) {

            throw new ResponseStatusException(HttpStatus.CONFLICT, "Não é possível transferir uma atribuição desativada.");
        }

        // Busca o novo funcionário
        User novoUsuario = userRepository.findById(dto.getNovoUsuarioId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Novo funcionário não encontrado."));

        if (!Boolean.TRUE.equals(novoUsuario.getAtivo())) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não é possível transferir para um usuário inativo.");
        }

        if (!"funcionario".equalsIgnoreCase(novoUsuario.getRole())) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A transferência deve ser feita para um funcionário.");
        }

        if (atual.getUsuario().getId().equals(novoUsuario.getId())) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O novo funcionário já é o responsável por esta atribuição.");
        }

        // Busca o administrador responsável pela operação
        User administrador = userRepository.findByEmail(emailAdministrador)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Administrador autenticado não encontrado."));

        // Desativa a atribuição anterior e garante a atualização no banco
        // Precisa garantir que a atribuição antiga seja desativada no banco, pois, 
        // tem um indice que impede duas atribuições ativas para o mesmo bloco ou sala.
        atual.setAtivo(false);
        assignmentRepository.saveAndFlush(atual);

        // Cria a nova atribuição preservando o mesmo destino
        AssignmentResponsibility nova = new AssignmentResponsibility();

        nova.setUsuario(novoUsuario);
        nova.setCriadoPor(administrador);
        nova.setAtivo(true);
        nova.setBloco(atual.getBloco());
        nova.setSala(atual.getSala());

        AssignmentResponsibility salva = assignmentRepository.save(nova);

        return AssignmentResponsibilityResponseDTO.fromEntity(salva);
    }

}
