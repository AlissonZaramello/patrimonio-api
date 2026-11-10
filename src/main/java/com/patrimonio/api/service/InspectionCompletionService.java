package com.patrimonio.api.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.patrimonio.api.exception.RegraNegocioException;
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
public class InspectionCompletionService {

    private final InspectionRepository conferenciaRepository;
    private final InspectionItemRepository itemRepository;
    private final UserRepository usuarioRepository;

    public record CompletionResponse(
            UUID conferenciaId,
            String status,
            LocalDateTime encerradaEm,
            Integer totalEsperados,
            Integer totalEncontrados,
            Integer totalAusentes,
            Integer totalDivergentes ) {}

    @Transactional
    public CompletionResponse finalizar(UUID conferenciaId, String emailUsuario) {

        User usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));

        if (!Boolean.TRUE.equals(usuario.getAtivo())) {

            throw new AccessDeniedException("Usuário inativo.");
        }

        Inspection conferencia = conferenciaRepository.findComBloqueioById(conferenciaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conferência não encontrada."));

        if (!"EM_ANDAMENTO".equals(conferencia.getStatus())) {

            throw new RegraNegocioException("Somente conferências em andamento podem ser finalizadas.");
        }

        if (conferencia.getOperador() == null || !conferencia.getOperador().getId().equals(usuario.getId())) {

            throw new AccessDeniedException("Você não é o operador desta conferência.");
        }

        // Valida o período do ciclo, quando existir.
        if (conferencia.getCiclo() != null) {

            LocalDate hoje = LocalDate.now();

            if (!"ATIVO".equals(conferencia.getCiclo().getStatus()) || hoje.isBefore(conferencia.getCiclo().getDataInicio()) || hoje.isAfter(conferencia.getCiclo().getDataFim())) {

                throw new RegraNegocioException("A conferência está fora do período permitido.");
            }
        }

        // Todos os itens ainda pendentes passam a AUSENTE.
        List<InspectionItem> pendentes = itemRepository.findByConferencia_IdAndResultado(conferenciaId, "PENDENTE");

        for (InspectionItem item : pendentes) {

            item.setResultado("AUSENTE");
            item.setObservacao("Patrimônio não encontrado durante a conferência.");
        }

        itemRepository.saveAll(pendentes);

        // Persiste as alterações antes de recalcular os totais.
        itemRepository.flush();

        int encontrados = Math.toIntExact(itemRepository.countByConferencia_IdAndResultado(conferenciaId, "ENCONTRADO"));

        int ausentes = Math.toIntExact(itemRepository.countByConferencia_IdAndResultado(conferenciaId, "AUSENTE"));

        int divergentes = Math.toIntExact(itemRepository.countByConferencia_IdAndResultado(conferenciaId, "DIVERGENTE"));

        conferencia.setTotalEncontrados(encontrados);
        conferencia.setTotalAusentes(ausentes);
        conferencia.setTotalDivergentes(divergentes);

        conferencia.setEncerradaEm(LocalDateTime.now());
        conferencia.setStatus("CONCLUIDA");

        conferenciaRepository.save(conferencia);

        return new CompletionResponse(
                conferencia.getId(),
                conferencia.getStatus(),
                conferencia.getEncerradaEm(),
                conferencia.getTotalEsperados(),
                conferencia.getTotalEncontrados(),
                conferencia.getTotalAusentes(),
                conferencia.getTotalDivergentes()
        );
    }
}
