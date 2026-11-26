package com.patrimonio.api.service;

import java.util.List;
import java.util.UUID;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.patrimonio.api.dto.BlockResponseDTO;
import com.patrimonio.api.model.User;
import com.patrimonio.api.repository.BlockRepository;
import com.patrimonio.api.repository.RoomRepository;
import com.patrimonio.api.repository.UserRepository;
import com.patrimonio.api.dto.BlockRequestDTO;
import com.patrimonio.api.model.Block;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class BlockService {

    private final BlockRepository blockRepository;
    private final UserRepository userRepository;
    private final AssignmentResponsibilityService assignmentService;
    private final RoomRepository roomRepository;    

    @Transactional(readOnly = true) 
    public List<BlockResponseDTO> listAll(String email, boolean administrador){
        
        if (administrador) {

            return blockRepository.findAll().stream().map(BlockResponseDTO::fromEntity).toList();
        }

        User usuario = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Usuario autenticado não encontrado."));

        return assignmentService.buscarBlocosDoUsuario(usuario.getId()).stream()
                .filter(bloco -> Boolean.TRUE.equals(bloco.getAtivo())).map(BlockResponseDTO::fromEntity).toList();

    }

    @Transactional
    public BlockResponseDTO criar(BlockRequestDTO dados) {

        String codigo = dados.codigo().trim().toUpperCase();
        String nome = dados.nome().trim();

        boolean codigoExiste = blockRepository.findAll()
                .stream().anyMatch(bloco -> bloco.getCodigo().equalsIgnoreCase(codigo));

        if (codigoExiste) {

            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um bloco cadastrado com este código.");
        }

        Block bloco = new Block();

        bloco.setCodigo(codigo);
        bloco.setNome(nome);
        bloco.setDescricao(dados.descricao() == null ? null : dados.descricao().trim());
        bloco.setAtivo(true);
        bloco.setCriadoEm(LocalDateTime.now());

        Block blocoSalvo = blockRepository.save(bloco);

        return BlockResponseDTO.fromEntity(blocoSalvo);
    }


    @Transactional
    public BlockResponseDTO atualizar(UUID id, BlockRequestDTO dados) {

        Block bloco = blockRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bloco não encontrado."));

        String codigo = dados.codigo().trim().toUpperCase();
        String nome = dados.nome().trim();

        boolean codigoEmUso = blockRepository.existsByCodigoIgnoreCaseAndIdNot(codigo, id);

        if (codigoEmUso) {

            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe outro bloco com este código.");
        }

        bloco.setCodigo(codigo);
        bloco.setNome(nome);
        bloco.setDescricao(dados.descricao() == null ? null : dados.descricao().trim());

        Block blocoAtualizado = blockRepository.save(bloco);

        return BlockResponseDTO.fromEntity(blocoAtualizado);
    }

    @Transactional 
    public BlockResponseDTO alterarSituacao(UUID id, boolean ativo){

        Block bloco = blockRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bloco não encontrado"));

        if (Boolean.valueOf(ativo).equals(bloco.getAtivo())){

            return BlockResponseDTO.fromEntity(bloco);
        }
        
        if (!ativo && roomRepository.existsByBloco_Id(id)) {

            throw new ResponseStatusException(HttpStatus.CONFLICT, "Não é possivel desativar um bloco que possui salas vinculadas.");
        }

        bloco.setAtivo(ativo);
        Block blocoAtualizado = blockRepository.save(bloco);

        return BlockResponseDTO.fromEntity(blocoAtualizado);
    }

}
