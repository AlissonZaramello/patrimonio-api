package com.patrimonio.api.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.patrimonio.api.model.InspectionCycle;

public interface InspectionCycleRepository extends JpaRepository<InspectionCycle, UUID> {

    boolean existsByMesReferenciaAndAnoReferencia(Integer mesReferencia, Integer anoReferencia);

    List<InspectionCycle> findAllByOrderByAnoReferenciaDescMesReferenciaDesc();

    // Verifica se outro ciclo ja utiliza o mesmo mês e ano, sem considerar o proprio que esta sendo editado
    boolean existsByMesReferenciaAndAnoReferenciaAndIdNot(Integer mesReferencia, Integer anoReferencia, UUID id);
}
