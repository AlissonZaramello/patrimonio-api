package com.patrimonio.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.patrimonio.api.model.Room;

public interface RoomRepository extends JpaRepository<Room, UUID> {

    List<Room> findByIdIn(Collection<UUID> ids);

    List<Room> findByBloco_IdIn(Collection<UUID> blocoIds);
}
