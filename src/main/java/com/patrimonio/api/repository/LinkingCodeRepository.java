package com.patrimonio.api.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import com.patrimonio.api.model.LinkingCode;

import jakarta.persistence.LockModeType;

public interface LinkingCodeRepository extends JpaRepository<LinkingCode, UUID> {

    Optional<LinkingCode> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    List<LinkingCode>findByConferencia_IdAndUtilizadoEmIsNull(UUID conferenciaId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)Optional<LinkingCode> findComBloqueioByCodigo(String codigo);

}
