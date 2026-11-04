package com.patrimonio.api.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.patrimonio.api.model.TagNfc;

public interface TagNfcRepository extends JpaRepository<TagNfc, UUID> {

    Optional<TagNfc> findByUuidTag(String uuidTag);

    boolean existsByUuidTag(String uuidTag);
}
