package com.patrimonio.api.controller;

import com.patrimonio.api.dto.LoginRequestDTO;
import com.patrimonio.api.dto.LoginResponseDTO;
import com.patrimonio.api.service.AuthService;
import com.patrimonio.api.dto.ForgotPasswordRequestDTO;
import com.patrimonio.api.dto.ResetPasswordRequestDTO;
import com.patrimonio.api.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        
        return ResponseEntity.ok(authService.login(dto));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> solicitarRecuperacao(@Valid @RequestBody ForgotPasswordRequestDTO dto) {

        passwordResetService.solicitarRecuperacao(dto);

        return ResponseEntity.ok(Map.of("mensagem", "Se o e-mail estiver cadastrado, você receberá instruções para redefinir sua senha."));
    }

    public ResponseEntity<Map<String, String>> redefinirSenha(@Valid @RequestBody ResetPasswordRequestDTO dto) {

        try{

            passwordResetService.redefinirSenha(dto);
            return ResponseEntity.ok(Map.of("mensagem", "Senha redefinida com sucesso."));
            
        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(Map.of("mensagem", "Token inválido ou expirado."));
        }

    }
}
