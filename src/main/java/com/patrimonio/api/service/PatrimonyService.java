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
import com.patrimonio.api.dto.PatrimonyRequestDTO;
import com.patrimonio.api.exception.RegraNegocioException;
import com.patrimonio.api.exception.RecursoNaoEncontradoException;
import com.patrimonio.api.model.Patrimony;
import com.patrimonio.api.model.TagNfc;
import com.patrimonio.api.repository.RoomRepository;
import com.patrimonio.api.repository.TagNfcRepository;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class PatrimonyService {

    private final PatrimonyRepository patrimonyRepository;
    private final UserRepository userRepository;
    private final AssignmentResponsibilityService assignmentService;
    private final RoomRepository salaRepository;
    private final TagNfcRepository tagNfcRepository;

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

    @Transactional
    public PatrimonyResponseDTO criar(PatrimonyRequestDTO requisicao) {

        String numeroTombo = requisicao.numeroTombo().trim();

        if (patrimonyRepository.existsByNumeroTombo(numeroTombo)) {

            throw new RegraNegocioException("Já existe um patrimônio com este número de tombo.");
        }

        Room sala = salaRepository.findById(requisicao.salaId()).orElseThrow(() -> new RecursoNaoEncontradoException("Sala não encontrada."));

        TagNfc tag = null;

        if (requisicao.tagNfcId() != null) {
            tag = tagNfcRepository.findById(requisicao.tagNfcId()).orElseThrow(() -> new RecursoNaoEncontradoException("Tag NFC não encontrada."));

            if (!Boolean.TRUE.equals(tag.getAtiva())) {

                throw new RegraNegocioException("A tag NFC informada está inativa.");
            }

            // Evita associar a mesma tag a dois patrimônios.
            UUID tagId = tag.getId();
            boolean vinculada = patrimonyRepository.findAll().stream().anyMatch(p -> p.getTagNfc() != null && p.getTagNfc().getId().equals(tagId));

            if (vinculada) {

                throw new RegraNegocioException("Esta tag NFC já está vinculada a outro patrimônio.");
            }
        }

        Patrimony patrimonio = new Patrimony();

        patrimonio.setNumeroTombo(numeroTombo);
        patrimonio.setDescricao(requisicao.descricao());
        patrimonio.setValorAquisicao(requisicao.valorAquisicao());
        patrimonio.setDataAquisicao(requisicao.dataAquisicao());
        patrimonio.setEstadoConservacao(requisicao.estadoConservacao().trim());
        patrimonio.setFotoUrl(requisicao.fotoUrl());
        patrimonio.setRoom(sala);
        patrimonio.setTagNfc(tag);
        patrimonio.setAtivo(requisicao.ativo() == null ? true : requisicao.ativo());
        patrimonio.setCriadoEm(LocalDateTime.now());

        Patrimony salvo = patrimonyRepository.saveAndFlush(patrimonio);

        return PatrimonyResponseDTO.fromEntity(salvo);
    }
}
