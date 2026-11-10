
package com.patrimonio.api.service;

import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;


import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.patrimonio.api.dto.InspectionCycleRequestDTO;
import com.patrimonio.api.model.InspectionCycle;
import com.patrimonio.api.model.User;
import com.patrimonio.api.model.Room;
import com.patrimonio.api.model.Inspection;
import com.patrimonio.api.model.InspectionItem;
import com.patrimonio.api.model.Patrimony;
import com.patrimonio.api.repository.InspectionCycleRepository;
import com.patrimonio.api.repository.UserRepository;
import com.patrimonio.api.repository.RoomRepository;
import com.patrimonio.api.repository.InspectionRepository;
import com.patrimonio.api.repository.InspectionItemRepository;
import com.patrimonio.api.repository.PatrimonyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InspectionCycleService {

    private final InspectionCycleRepository cicloRepository;
    private final UserRepository userRepository;
    private final RoomRepository salaRepository;
    private final AssignmentResponsibilityService atribuicaoService;
    private final InspectionRepository conferenciaRepository;
    private final InspectionItemRepository itemConferenciaRepository;
    private final PatrimonyRepository patrimonioRepository;

    @Transactional(readOnly = true)
    public List<InspectionCycle> listarTodos() {

        return cicloRepository.findAllByOrderByAnoReferenciaDescMesReferenciaDesc();
    }

    @Transactional(readOnly = true)
    public InspectionCycle buscarPorId(UUID id) {

        return cicloRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ciclo de conferência não encontrado."));
    }

    @Transactional
    public InspectionCycle criar(InspectionCycleRequestDTO dto, String emailAdministrador) {

        // Valida as datas
        if (dto.getDataFim().isBefore(dto.getDataInicio())) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A data final não pode ser anterior à data inicial.");
        }

        // Verifica se já existe um ciclo para o mês e ano
        if (cicloRepository.existsByMesReferenciaAndAnoReferencia(dto.getMesReferencia(), dto.getAnoReferencia())) {

            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um ciclo cadastrado para este mês e ano.");
        }

        // Busca o administrador autenticado
        User administrador = userRepository.findByEmail(emailAdministrador)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Administrador autenticado não encontrado."));

        // Cria o ciclo
        InspectionCycle ciclo = new InspectionCycle();

        ciclo.setNome(dto.getNome());
        ciclo.setMesReferencia(dto.getMesReferencia());
        ciclo.setAnoReferencia(dto.getAnoReferencia());
        ciclo.setDataInicio(dto.getDataInicio());
        ciclo.setDataFim(dto.getDataFim());
        ciclo.setStatus("PLANEJADO");
        ciclo.setCriadoPor(administrador);

        return cicloRepository.save(ciclo);
    }

    @Transactional
    public InspectionCycle atualizar(UUID id, InspectionCycleRequestDTO dto) {

        InspectionCycle ciclo = buscarPorId(id);

        if (!"PLANEJADO".equals(ciclo.getStatus())) {

            throw new ResponseStatusException(HttpStatus.CONFLICT, "Somente ciclos planejados podem ser editados.");
        }

        if (dto.getDataFim().isBefore(dto.getDataInicio())) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A data final não pode ser anterior à data inicial.");
        }

        if (cicloRepository.existsByMesReferenciaAndAnoReferenciaAndIdNot(dto.getMesReferencia(), dto.getAnoReferencia(), id)) {

            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe outro ciclo cadastrado para este mês e ano.");
        }

        ciclo.setNome(dto.getNome());
        ciclo.setMesReferencia(dto.getMesReferencia());
        ciclo.setAnoReferencia(dto.getAnoReferencia());
        ciclo.setDataInicio(dto.getDataInicio());
        ciclo.setDataFim(dto.getDataFim());

        return cicloRepository.save(ciclo);
    }

    @Transactional
    public InspectionCycle cancelar(UUID id) {

        InspectionCycle ciclo = buscarPorId(id);

        if ("CANCELADO".equals(ciclo.getStatus())) {

            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este ciclo já está cancelado.");
        }

        if ("CONCLUIDO".equals(ciclo.getStatus())) {

            throw new ResponseStatusException(HttpStatus.CONFLICT, "Não é possível cancelar um ciclo concluído.");
        }

        ciclo.setStatus("CANCELADO");

        return cicloRepository.save(ciclo);
    }

    @Transactional
    public InspectionCycle ativar(UUID id) {

        // Busca o ciclo
        InspectionCycle ciclo = buscarPorId(id);

        // Verifica se o ciclo está planejado
        if (!"PLANEJADO".equals(ciclo.getStatus())) {

            throw new ResponseStatusException(HttpStatus.CONFLICT, "Somente ciclos planejados podem ser ativados.");
        }

        // Impede uma geração duplicada
        if (conferenciaRepository.countByCiclo_Id(id) > 0) {

            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este ciclo ja possui conferências geradas.");
        }

        // Busca todas as salas cadastradas
        List<Room> salas = salaRepository.findAll();

        if (salas.isEmpty()) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não existem salas cadastradas para gerar as conferências.");
        }

        // Identifica os responsáveis de cada sala
        Map<UUID, User> responsaveisPorSala = new HashMap<>();
        List<String> salasSemResponsavel = new ArrayList<>();

        for (Room sala : salas) {

            var responsavel = atribuicaoService.buscarResponsavelDaSala(sala.getId());

            if (responsavel.isEmpty() || !Boolean.TRUE.equals(responsavel.get().getAtivo()) || !"funcionario".equalsIgnoreCase(responsavel.get().getRole())) {

                salasSemResponsavel.add(sala.getId().toString());
                continue;
            }

            responsaveisPorSala.put(sala.getId(), responsavel.get());
        }

        // Impede a ativação se alguma sala não tiver responsável válido
        if (!salasSemResponsavel.isEmpty()) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não é possível ativar o ciclo. As seguintes salas não possuem " + "um responsável ativo válido: " + String.join(", ", salasSemResponsavel));
        }

        // Busca os patrimonios ativos de todas as salas
        List<UUID> idsSalas = salas.stream().map(Room::getId).toList();

        List<Patrimony> patrimonios = patrimonioRepository.findByRoom_IdInAndAtivoTrue(idsSalas);

        // Conta os patrimônios esperados por sala
        Map<UUID, Integer> quantidadesPorSala = new HashMap<>();

        for (Patrimony patrimonio : patrimonios) {

            UUID salaId = patrimonio.getRoom().getId();

            quantidadesPorSala.merge(salaId, 1, Integer::sum);
        }

        // Cria uma conferência para cada sala
        List<Inspection> conferencias = new ArrayList<>();

        for (Room sala : salas) {

            Inspection conferencia = new Inspection();

            conferencia.setCiclo(ciclo);
            conferencia.setSala(sala);
            conferencia.setResponsavel(responsaveisPorSala.get(sala.getId()));
            conferencia.setStatus("PENDENTE");

            conferencia.setTotalEsperados(quantidadesPorSala.getOrDefault(sala.getId(), 0));

            conferencias.add(conferencia);
        }

        // Persiste as conferências
        List<Inspection> conferenciasSalvas = conferenciaRepository.saveAll(conferencias);

        // Relaciona cada sala à sua conferência
        Map<UUID, Inspection> conferenciasPorSala = new HashMap<>();

        for (Inspection conferencia : conferenciasSalvas) {

            conferenciasPorSala.put(conferencia.getSala().getId(),conferencia);
        }

        // Cria os itens esperados de cada conferencia
        List<InspectionItem> itens = new ArrayList<>();

        for (Patrimony patrimonio : patrimonios) {

            UUID salaId = patrimonio.getRoom().getId();

            InspectionItem item = new InspectionItem();

            item.setConferencia(conferenciasPorSala.get(salaId));
            item.setPatrimonio(patrimonio);
            item.setTagNfc(patrimonio.getTagNfc());
            item.setSalaEsperada(patrimonio.getRoom());
            item.setResultado("PENDENTE");

            itens.add(item);
        }

        // Persiste todos os itens
        itemConferenciaRepository.saveAll(itens);

        // Ativa o ciclo
        ciclo.setStatus("ATIVO");

        return cicloRepository.save(ciclo);
    }

    
}
