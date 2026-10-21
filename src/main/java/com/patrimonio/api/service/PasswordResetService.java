package com.patrimonio.api.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

import com.patrimonio.api.dto.ForgotPasswordRequestDTO;
import com.patrimonio.api.dto.ResetPasswordRequestDTO;
import com.patrimonio.api.model.PasswordResetToken;
import com.patrimonio.api.model.User;
import com.patrimonio.api.repository.PasswordResetTokenRepository;
import com.patrimonio.api.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private static final int TOKEN_BYTES = 32;
    private static final int TOKEN_VALIDITY_MINUTES = 15;

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Transactional
    public void solicitarRecuperacao(ForgotPasswordRequestDTO dto) {

        String email = dto.getEmail().trim();

        User user = userRepository.findByEmail(email).orElse(null);

        // Não revela se o e-mail existe no sistema.
        if (user == null || Boolean.FALSE.equals(user.getAtivo())) {
            return;
        }

        // Invalida tokens anteriores ainda não utilizados.
        tokenRepository.findAll().stream()
                .filter(token -> token.getUser().getId().equals(user.getId()))
                .filter(token -> token.getUsedAt() == null)
                .forEach(token -> {
                    token.setUsedAt(LocalDateTime.now());
                });

        byte[] bytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(bytes);

        String tokenOriginal = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUser(user);
        resetToken.setTokenHash(gerarHash(tokenOriginal));
        resetToken.setExpiresAt(
                LocalDateTime.now().plusMinutes(TOKEN_VALIDITY_MINUTES)
        );

        tokenRepository.save(resetToken);
        
        String linkRecuperacao = frontendUrl + "/resetar-senha?token=" + java.net.URLEncoder.encode(tokenOriginal, java.nio.charset.StandardCharsets.UTF_8);
        
        emailService.enviarEmailRecuperacao(user.getEmail(), linkRecuperacao);

        /*
         * O envio do e-mail será implementado na próxima etapa.
         * O token original não deve ser salvo no banco nem retornado
         * ao usuário por uma API de produção.
         */
    }

    @Transactional
    public void redefinirSenha(ResetPasswordRequestDTO dto) {

        String tokenHash = gerarHash(dto.getToken());

        PasswordResetToken resetToken = tokenRepository
                .findByTokenHash(tokenHash)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Token inválido ou expirado."
                        )
                );

        if (resetToken.getUsedAt() != null) {
            throw new IllegalArgumentException(
                    "Token inválido ou expirado."
            );
        }

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "Token inválido ou expirado."
            );
        }

        User user = resetToken.getUser();

        user.setSenhaHash(passwordEncoder.encode(dto.getNovaSenha()));
        user.setAtualizadoEm(LocalDateTime.now());

        userRepository.save(user);

        resetToken.setUsedAt(LocalDateTime.now());
        tokenRepository.save(resetToken);
    }

    private String gerarHash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                    "Não foi possível gerar o hash do token.", e
            );
        }
    }
}