package com.patrimonio.api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.patrimonio.api.dto.PatrimonyResponseDTO;
import com.patrimonio.api.repository.PatrimonyRepository;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class PatrimonyService {

    private final PatrimonyRepository patrimonyRepository;

    public List<PatrimonyResponseDTO> listAll(){
        return patrimonyRepository.findAll()
                .stream()
                .map(PatrimonyResponseDTO::fromEntity)
                .toList();
    }
}
