package com.patrimonio.api.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.patrimonio.api.model.Inspection;

public interface InspectionRepository extends JpaRepository<Inspection, UUID> {

    List<Inspection> findByCiclo_Id(UUID cicloId);

    List<Inspection> findByResponsavel_Id(UUID responsavelId);

    List<Inspection> findByCiclo_IdAndResponsavel_Id(UUID cicloId, UUID responsavelId);

    boolean existsByCiclo_IdAndSala_Id(UUID cicloId, UUID salaId);

    long countByCiclo_Id(UUID cicloId);
}