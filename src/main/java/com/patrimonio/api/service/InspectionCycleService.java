
package com.patrimonio.api.service;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.patrimonio.api.dto.InspectionCycleRequestDTO;
import com.patrimonio.api.model.InspectionCycle;
import com.patrimonio.api.model.User;
import com.patrimonio.api.repository.InspectionCycleRepository;
import com.patrimonio.api.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InspectionCycleService {

    private final InspectionCycleRepository cicloRepository;
    private final UserRepository userRepository;

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

}
