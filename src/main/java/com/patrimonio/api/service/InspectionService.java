
package com.patrimonio.api.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.patrimonio.api.exception.RecursoNaoEncontradoException;
import com.patrimonio.api.exception.RegraNegocioException;
import com.patrimonio.api.model.Inspection;
import com.patrimonio.api.model.User;
import com.patrimonio.api.repository.InspectionRepository;
import com.patrimonio.api.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InspectionService {

    private final InspectionRepository conferenciaRepository;
    private final UserRepository usuarioRepository;

    @Transactional
    public Inspection iniciar(UUID conferenciaId, String emailUsuario) {

        Inspection conferencia = conferenciaRepository.findById(conferenciaId).orElseThrow(() -> new RecursoNaoEncontradoException("Conferência não encontrada."));

        User operador = usuarioRepository.findByEmail(emailUsuario).orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));

        if (!Boolean.TRUE.equals(operador.getAtivo())) {

            throw new RegraNegocioException("Usuário inativo.");
        }

        if (!conferencia.getResponsavel().getId().equals(operador.getId())) {

            throw new AccessDeniedException("Você não tem autorização para executar esta conferência.");
        }

        if (!"PENDENTE".equals(conferencia.getStatus())) {

            throw new RegraNegocioException("Esta conferência não está disponível para início.");
        }

        if (conferencia.getCiclo() != null) {

            LocalDate hoje = LocalDate.now();

            if (hoje.isBefore(conferencia.getCiclo().getDataInicio()) || hoje.isAfter(conferencia.getCiclo().getDataFim())) {

                throw new RegraNegocioException("A conferência só pode ser executada dentro do período do ciclo.");
            }

            if (!"ATIVO".equals(conferencia.getCiclo().getStatus())) {
                
                throw new RegraNegocioException("O ciclo de conferência não está ativo.");
            }
        }

        conferencia.setOperador(operador);
        conferencia.setIniciadaEm(LocalDateTime.now());
        conferencia.setStatus("EM_ANDAMENTO");

        return conferenciaRepository.save(conferencia);
    }
}
