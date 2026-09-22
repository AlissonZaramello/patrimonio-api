package com.patrimonio.api.repository;

import com.patrimonio.api.model.Block;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;


public interface BlockRepository extends JpaRepository<Block, Long> {
}
