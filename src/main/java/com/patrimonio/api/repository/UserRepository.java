package com.patrimonio.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.patrimonio.api.model.User;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByActive(boolean active);
}
