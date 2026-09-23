package com.patrimonio.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import com.patrimonio.api.dto.PatrimonyResponseDTO;
import com.patrimonio.api.service.PatrimonyService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/patrimonios")
@RequiredArgsConstructor  
public class PatrimonyController {

    private final PatrimonyService patrimonyService;

    @GetMapping
    public ResponseEntity<List<PatrimonyResponseDTO>> listAll(){
        return ResponseEntity.ok(patrimonyService.listAll());
    }
}
