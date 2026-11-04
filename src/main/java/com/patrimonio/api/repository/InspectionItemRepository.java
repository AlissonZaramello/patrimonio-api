package com.patrimonio.api.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.patrimonio.api.model.InspectionItem;

public interface InspectionItemRepository extends JpaRepository<InspectionItem, UUID> {

    List<InspectionItem> findByConferencia_Id(UUID conferenciaId);

    List<InspectionItem> findByConferencia_IdAndStatus(UUID conferenciaId, String status);

    List<InspectionItem> findByPatrimonio_Id(UUID patrimonioId);

    long countByConferencia_Id(UUID conferenciaId);

    long countByConferencia_IdAndStatus(UUID conferenciaId, String status);
}