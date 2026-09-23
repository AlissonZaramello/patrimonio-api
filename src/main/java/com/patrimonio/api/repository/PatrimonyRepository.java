package com.patrimonio.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.patrimonio.api.model.Patrimony;

public interface PatrimonyRepository extends JpaRepository<Patrimony, Long> {

    List<Patrimony> findByAtivo(Boolean ativo);
}
