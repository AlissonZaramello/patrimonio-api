package com.patrimonio.api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.patrimonio.api.dto.BlockResponseDTO;
import com.patrimonio.api.repository.BlockRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class BlockService {

    private final BlockRepository blockRepository;

    public List<BlockResponseDTO> listAll(){
        return blockRepository.findAll()
                .stream()
                .map(BlockResponseDTO::fromEntity)
                .toList();
    }
}
