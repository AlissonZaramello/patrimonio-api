package com.patrimonio.api.repository;

import com.patrimonio.api.model.Block;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;


public interface BlockRepository extends JpaRepository<Block, UUID> {
}
