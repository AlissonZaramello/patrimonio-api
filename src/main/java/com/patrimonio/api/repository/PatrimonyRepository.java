package com.patrimonio.api.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.patrimonio.api.model.Patrimony;

public interface PatrimonyRepository extends JpaRepository<Patrimony, UUID> {

    List<Patrimony> findByAtivo(Boolean ativo);
}
