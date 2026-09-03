package com.patrimonio.api.controller;

import com.patrimonio.api.dto.UserResponseDTO;
import com.patrimonio.api.service.UserService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> listAll(){
        return ResponseEntity.ok(userService.listAll());
    }
}
