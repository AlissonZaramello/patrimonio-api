package com.patrimonio.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.patrimonio.api.model.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    List<User> findByAtivo(Boolean ativo);

    Optional<User> findByEmail(String email);
}
