package com.patrimonio.api.service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.patrimonio.api.exception.RegraNegocioException;
import com.patrimonio.api.exception.RecursoNaoEncontradoException;
import com.patrimonio.api.model.LinkingCode;
import com.patrimonio.api.model.Inspection;
import com.patrimonio.api.model.User;
import com.patrimonio.api.repository.LinkingCodeRepository;
import com.patrimonio.api.repository.InspectionRepository;
import com.patrimonio.api.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InspectionLinkService {

    private final LinkingCodeRepository codigoRepository;
    private final InspectionRepository conferenciaRepository;
    private final UserRepository usuarioRepository;

    private static final SecureRandom GERADOR = new SecureRandom();
    private static final String CARACTERES = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    public record GeneratedCode(String codigo, LocalDateTime expiraEm) {}

    public record LinkedInspection(UUID conferenciaId, UUID salaId, String status) {}

    private User buscarUsuario(String email) {

        User usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));

        if (!Boolean.TRUE.equals(usuario.getAtivo())) {

            throw new RegraNegocioException("Usuário inativo.");
        }

        return usuario;
    }

    private void verificarResponsavel(Inspection conferencia, User usuario) {

        if (!conferencia.getResponsavel().getId().equals(usuario.getId())) {

            throw new AccessDeniedException("Você não é o responsável por esta conferência.");
        }

    }

    private void validarPeriodo(Inspection conferencia) {

        if (conferencia.getCiclo() == null) {

            return;
        }

        if (!"ATIVO".equals(conferencia.getCiclo().getStatus())) {

            throw new RegraNegocioException("O ciclo de conferência não está ativo.");
        }

        LocalDate hoje = LocalDate.now();

        if (hoje.isBefore(conferencia.getCiclo().getDataInicio()) || hoje.isAfter(conferencia.getCiclo().getDataFim())) {

            throw new RegraNegocioException( "A conferência está fora do período permitido.");
        }
    }

    private String gerarCodigoAleatorio() {

        String codigo;

        do {
            StringBuilder valor = new StringBuilder();

            for (int i = 0; i < 10; i++) {

                int indice = GERADOR.nextInt(CARACTERES.length());
                valor.append(CARACTERES.charAt(indice));
            }

            codigo = valor.toString();

        } while (codigoRepository.existsByCodigo(codigo));

        return codigo;
    }

    @Transactional
    public GeneratedCode gerarCodigo(UUID conferenciaId, String emailUsuario) {

        User usuario = buscarUsuario(emailUsuario);

        Inspection conferencia = conferenciaRepository.findComBloqueioById(conferenciaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conferência não encontrada."));

        verificarResponsavel(conferencia, usuario);

        if (!"PENDENTE".equals(conferencia.getStatus())) {

            throw new RegraNegocioException("Somente conferências pendentes podem gerar código.");
        }

        validarPeriodo(conferencia);

        LocalDateTime agora = LocalDateTime.now();

        // Invalida códigos anteriores ainda não utilizados.
        for (LinkingCode anterior : codigoRepository.findByConferencia_IdAndUtilizadoEmIsNull(conferenciaId)) {

            anterior.setExpiraEm(agora);
        }

        LinkingCode novo = new LinkingCode();

        novo.setCodigo(gerarCodigoAleatorio());
        novo.setConferencia(conferencia);
        novo.setSolicitante(usuario);
        novo.setCriadoEm(agora);
        novo.setExpiraEm(agora.plusMinutes(5));

        codigoRepository.save(novo);

        return new GeneratedCode(novo.getCodigo(), novo.getExpiraEm());
    }

    @Transactional
    public LinkedInspection vincular(String codigoInformado, String emailUsuario) {

        User usuario = buscarUsuario(emailUsuario);

        if (codigoInformado == null || codigoInformado.isBlank()) {

            throw new RegraNegocioException("Informe o código de vinculação.");
        }

        String codigo = codigoInformado.trim().toUpperCase();

        LinkingCode localizado = codigoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new RegraNegocioException("Código inválido ou inexistente."));

        // Bloqueia a conferência antes de consumir o código.
        Inspection conferencia = conferenciaRepository.findComBloqueioById(localizado.getConferencia().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conferência não encontrada."));

        LinkingCode vinculacao = codigoRepository.findComBloqueioByCodigo(codigo)
                .orElseThrow(() -> new RegraNegocioException("Código inválido."));

        verificarResponsavel(conferencia, usuario);

        if (!vinculacao.getSolicitante().getId().equals(usuario.getId())) {

            throw new AccessDeniedException("Este código foi gerado por outro usuário.");
        }

        LocalDateTime agora = LocalDateTime.now();

        if (vinculacao.getUtilizadoEm() != null) {

            throw new RegraNegocioException("Este código já foi utilizado.");
        }

        if (!agora.isBefore(vinculacao.getExpiraEm())) {

            throw new RegraNegocioException("Este código expirou.");
        }

        if (!"PENDENTE".equals(conferencia.getStatus())) {
            
            throw new RegraNegocioException("Esta conferência já foi iniciada ou encerrada.");
        }

        validarPeriodo(conferencia);

        conferencia.setOperador(usuario);
        conferencia.setIniciadaEm(agora);
        conferencia.setStatus("EM_ANDAMENTO");

        vinculacao.setUtilizadoEm(agora);

        conferenciaRepository.save(conferencia);
        codigoRepository.save(vinculacao);

        return new LinkedInspection(conferencia.getId(), conferencia.getSala().getId(), conferencia.getStatus());
    }

}
