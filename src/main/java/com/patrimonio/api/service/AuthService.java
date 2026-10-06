package com.patrimonio.api.service;

import com.patrimonio.api.dto.LoginRequestDTO;
import com.patrimonio.api.dto.LoginResponseDTO;
import com.patrimonio.api.model.User;
import com.patrimonio.api.repository.UserRepository;
import com.patrimonio.api.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public LoginResponseDTO login(LoginRequestDTO dto) {
        // Delega ao Spring Security a checagem de email/senha (usa o PasswordEncoder configurado)
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getSenha())
            );
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("E-mail ou senha inválidos");
        }

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new BadCredentialsException("E-mail ou senha inválidos"));

        if (Boolean.FALSE.equals(user.getAtivo())) {
            throw new BadCredentialsException("Usuário inativo. Contate o administrador.");
        }

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getSenhaHash())
                .build();

        String token = jwtService.gerarToken(userDetails, user.getRole());

        return new LoginResponseDTO(token, user.getId(), user.getNome(), user.getEmail(), user.getRole());
    }
}
