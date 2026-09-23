package com.patrimonio.api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.patrimonio.api.dto.RoomResponseDTO;
import com.patrimonio.api.repository.RoomRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class RoomService {

    private final RoomRepository roomRepository;

    public List<RoomResponseDTO> listAll(){
        return roomRepository.findAll()
                .stream()
                .map(RoomResponseDTO::fromEntity)
                .toList();
    }
}
