package com.patrimonio.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

import com.patrimonio.api.model.Room;

public interface RoomRepository extends JpaRepository<Room, UUID> {

}
