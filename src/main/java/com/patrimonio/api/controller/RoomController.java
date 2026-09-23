package com.patrimonio.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.patrimonio.api.dto.RoomResponseDTO;
import com.patrimonio.api.service.RoomService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/salas")
@RequiredArgsConstructor  
public class RoomController {

    private final RoomService roomService;

    @GetMapping 
    public ResponseEntity<List<RoomResponseDTO>> listAll(){
        return ResponseEntity.ok(roomService.listAll());
    }
}
