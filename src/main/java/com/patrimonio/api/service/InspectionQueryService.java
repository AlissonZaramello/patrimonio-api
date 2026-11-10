package com.patrimonio.api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.patrimonio.api.exception.RecursoNaoEncontradoException;
import com.patrimonio.api.model.Inspection;
import com.patrimonio.api.model.InspectionItem;
import com.patrimonio.api.model.User;
import com.patrimonio.api.repository.InspectionItemRepository;
import com.patrimonio.api.repository.InspectionRepository;
import com.patrimonio.api.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InspectionQueryService {

    private final InspectionRepository conferenciaRepository;
    private final InspectionItemRepository itemRepository;
    private final UserRepository usuarioRepository;

    public record InspectionResponse(
            UUID id,
            UUID cicloId,
            UUID salaId,
            UUID responsavelId,
            UUID operadorId,
            String status,
            LocalDateTime iniciadaEm,
            LocalDateTime encerradaEm,
            Integer totalEsperados,
            Integer totalEncontrados,
            Integer totalAusentes,
            Integer totalDivergentes ) {}

    public record InspectionItemResponse(
            UUID id,
            UUID patrimonioId,
            UUID tagNfcId,
            UUID salaEsperadaId,
            UUID salaLidaId,
            String resultado,
            LocalDateTime lidoEm,
            String observacao ) {}

    private User buscarUsuario(Authentication autenticacao) {

        return usuarioRepository.findByEmail(autenticacao.getName()).filter(usuario -> Boolean.TRUE.equals(usuario.getAtivo()))
                .orElseThrow(() -> new AccessDeniedException("Usuário não autorizado."));
    }

    private boolean ehAdministrador(Authentication autenticacao) {

        return autenticacao.getAuthorities().stream().anyMatch(autoridade -> "ROLE_ADMIN".equals(autoridade.getAuthority()) || "ROLE_ADMINISTRADOR".equals(autoridade.getAuthority()));
    }

    private void verificarAcesso(Inspection conferencia, User usuario, Authentication autenticacao) {

        if (ehAdministrador(autenticacao)) {

            return;
        }

        if (!conferencia.getResponsavel().getId().equals(usuario.getId())) {

            throw new AccessDeniedException("Você não tem autorização para consultar esta conferência.");
        }
    }

    private Inspection buscarConferencia(UUID id, User usuario, Authentication autenticacao) {

        Inspection conferencia = conferenciaRepository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Conferência não encontrada."));

        verificarAcesso(conferencia, usuario, autenticacao);

        return conferencia;
    }

    private InspectionResponse converter(Inspection conferencia) {

        return new InspectionResponse(
                conferencia.getId(),
                conferencia.getCiclo() != null ? conferencia.getCiclo().getId() : null,
                conferencia.getSala().getId(),
                conferencia.getResponsavel().getId(),
                conferencia.getOperador() != null ? conferencia.getOperador().getId() : null,
                conferencia.getStatus(),
                conferencia.getIniciadaEm(),
                conferencia.getEncerradaEm(),
                conferencia.getTotalEsperados(),
                conferencia.getTotalEncontrados(),
                conferencia.getTotalAusentes(),
                conferencia.getTotalDivergentes()
        );
    }

    @Transactional(readOnly = true)
    public List<InspectionResponse> listar(Authentication autenticacao) {

        User usuario = buscarUsuario(autenticacao);

        List<Inspection> conferencias;

        if (ehAdministrador(autenticacao)) {

            conferencias = conferenciaRepository.findAll();
        } else {

            conferencias = conferenciaRepository.findByResponsavel_Id(usuario.getId());
        }

        return conferencias.stream().map(this::converter).toList();
    }

    @Transactional(readOnly = true)
    public InspectionResponse buscarPorId(UUID id, Authentication autenticacao) {

        User usuario = buscarUsuario(autenticacao);

        return converter(buscarConferencia(id, usuario, autenticacao));
    }

    @Transactional(readOnly = true)
    public List<InspectionItemResponse> listarItens(UUID conferenciaId, Authentication autenticacao) {

        User usuario = buscarUsuario(autenticacao);

        buscarConferencia(conferenciaId, usuario, autenticacao);

        return itemRepository.findByConferencia_Id(conferenciaId).stream()
                .map(item -> new InspectionItemResponse(
                        item.getId(),
                        item.getPatrimonio().getId(),
                        item.getTagNfc() != null ? item.getTagNfc().getId() : null,
                        item.getSalaEsperada().getId(),
                        item.getSalaLida() != null ? item.getSalaLida().getId() : null,
                        item.getResultado(),
                        item.getLidoEm(),
                        item.getObservacao()
                )).toList();
    }
}
