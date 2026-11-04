package com.patrimonio.api.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.patrimonio.api.model.AssignmentResponsibility;

public interface AssignmentResponsibilityRepository extends JpaRepository<AssignmentResponsibility, UUID> {

    List<AssignmentResponsibility> findByUsuario_IdAndAtivoTrue(UUID usuarioId);

    Optional<AssignmentResponsibility> findByBloco_IdAndAtivoTrue(UUID blocoId);

    Optional<AssignmentResponsibility> findBySala_IdAndAtivoTrue(UUID salaId);

    List<AssignmentResponsibility> findByBloco_IdInAndAtivoTrue(Collection<UUID> blocoIds);

    List<AssignmentResponsibility> findBySala_IdInAndAtivoTrue(Collection<UUID> salaIds);

    boolean existsByBloco_IdAndAtivoTrue(UUID blocoId);

    boolean existsBySala_IdAndAtivoTrue(UUID salaId);
}
