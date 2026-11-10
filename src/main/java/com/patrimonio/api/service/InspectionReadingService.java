package com.patrimonio.api.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.patrimonio.api.exception.RegraNegocioException;
import com.patrimonio.api.exception.RecursoNaoEncontradoException;
import com.patrimonio.api.model.Inspection;
import com.patrimonio.api.model.InspectionItem;
import com.patrimonio.api.model.Patrimony;
import com.patrimonio.api.model.User;
import com.patrimonio.api.repository.InspectionItemRepository;
import com.patrimonio.api.repository.InspectionRepository;
import com.patrimonio.api.repository.PatrimonyRepository;
import com.patrimonio.api.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InspectionReadingService {

    private final InspectionRepository conferenciaRepository;
    private final InspectionItemRepository itemRepository;
    private final UserRepository usuarioRepository;
    private final PatrimonyRepository patrimonioRepository;

    public record ReadingResponse(
            UUID itemId,
            UUID patrimonioId,
            UUID salaId,
            String resultado,
            LocalDateTime lidoEm,
            Integer totalEncontrados,
            Integer totalDivergentes ) {}

    @Transactional
    public ReadingResponse registrarLeitura(UUID conferenciaId, String uuidTag, String emailUsuario) {

        User usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));

        if (!Boolean.TRUE.equals(usuario.getAtivo())) {

            throw new AccessDeniedException("Usuário inativo.");
        }

        Inspection conferencia = conferenciaRepository.findComBloqueioById(conferenciaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conferência não encontrada."));

        if (!"EM_ANDAMENTO".equals(conferencia.getStatus())) {

            throw new RegraNegocioException("A conferência não está em andamento.");
        }

        if (conferencia.getOperador() == null || !conferencia.getOperador().getId().equals(usuario.getId())) {

            throw new AccessDeniedException("Você não é o operador desta conferência.");
        }

        if (conferencia.getCiclo() != null) {

            LocalDate hoje = LocalDate.now();

            if (!"ATIVO".equals(conferencia.getCiclo().getStatus()) || hoje.isBefore(conferencia.getCiclo().getDataInicio()) || hoje.isAfter(conferencia.getCiclo().getDataFim())) {

                throw new RegraNegocioException("A conferência está fora do período permitido.");
            }
        }

        if (uuidTag == null || uuidTag.isBlank()) {

            throw new RegraNegocioException("Informe o identificador da tag NFC.");
        }

        String identificador = uuidTag.trim();

        Patrimony patrimonio = patrimonioRepository.findByTagNfc_UuidTag(identificador)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Nenhum patrimônio encontrado para esta tag NFC."));

        if (!Boolean.TRUE.equals(patrimonio.getAtivo())) {

            throw new RegraNegocioException("O patrimônio está inativo.");
        }

        if (patrimonio.getTagNfc() == null || !Boolean.TRUE.equals(patrimonio.getTagNfc().getAtiva())) {

            throw new RegraNegocioException("A tag NFC está inativa.");
        }

        if (patrimonio.getRoom() == null) {

            throw new RegraNegocioException("O patrimônio não possui uma sala cadastrada.");
        }

        InspectionItem item = itemRepository.findByConferencia_IdAndPatrimonio_Id(conferenciaId, patrimonio.getId()).orElse(null);

        if (item != null && !"PENDENTE".equals(item.getResultado())) {

            throw new RegraNegocioException("Este patrimônio já foi registrado nesta conferência.");
        }

        UUID salaConferenciaId = conferencia.getSala().getId();
        UUID salaPatrimonioId = patrimonio.getRoom().getId();

        boolean mesmaSala = salaConferenciaId.equals(salaPatrimonioId);

        if (mesmaSala && item == null) {

            throw new RegraNegocioException("O patrimônio pertence à sala, mas não consta " + "entre os itens esperados desta conferência.");
        }

        // Um patrimônio não esperado nesta sala gera
        // um registro adicional de divergência.
        if (item == null) {
            item = new InspectionItem();
            item.setConferencia(conferencia);
            item.setPatrimonio(patrimonio);
            item.setTagNfc(patrimonio.getTagNfc());
            item.setSalaEsperada(patrimonio.getRoom());
        }

        LocalDateTime agora = LocalDateTime.now();

        item.setSalaLida(conferencia.getSala());
        item.setLidoEm(agora);

        if (mesmaSala) {

            item.setResultado("ENCONTRADO");

            conferencia.setTotalEncontrados(conferencia.getTotalEncontrados() + 1);

        } else {

            item.setResultado("DIVERGENTE");

            item.setObservacao("Patrimônio cadastrado em outra sala.");

            conferencia.setTotalDivergentes(conferencia.getTotalDivergentes() + 1);
        }

        itemRepository.save(item);
        conferenciaRepository.save(conferencia);

        return new ReadingResponse(
                item.getId(),
                patrimonio.getId(),
                salaConferenciaId,
                item.getResultado(),
                item.getLidoEm(),
                conferencia.getTotalEncontrados(),
                conferencia.getTotalDivergentes()
        );
    }        
}
