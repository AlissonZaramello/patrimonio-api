package com.patrimonio.api.repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.patrimonio.api.model.Patrimony;

public interface PatrimonyRepository extends JpaRepository<Patrimony, UUID> {

    List<Patrimony> findByAtivo(Boolean ativo);

    List<Patrimony> findByRoom_IdIn(Collection<UUID> salaIds);

    List<Patrimony> findByRoom_IdInAndAtivoTrue(Collection<UUID> salaIds);

    Optional<Patrimony> findByTagNfc_UuidTag(String uuidTag);

    boolean existsByNumeroTombo(String numeroTombo);
}
