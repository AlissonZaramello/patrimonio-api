package com.patrimonio.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.patrimonio.api.model.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {

}
