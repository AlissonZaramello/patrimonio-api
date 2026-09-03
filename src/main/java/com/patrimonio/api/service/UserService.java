package com.patrimonio.api.service;

import org.springframework.stereotype.Service;
import com.patrimonio.api.dto.UserResponseDTO;
import com.patrimonio.api.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserResponseDTO> listAll(){
        return userRepository.findAll()
                .stream()
                .map(UserResponseDTO::fromEntity)
                .toList();
    }
}
