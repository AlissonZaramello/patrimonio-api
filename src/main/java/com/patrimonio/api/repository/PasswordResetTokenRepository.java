package com.patrimonio.api.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.patrimonio.api.model.PasswordResetToken;

public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, UUID> {

    Optional<PasswordResetToken> findByTokenHash(String tokenHash);

    @Modifying
    @Query("""
        UPDATE PasswordResetToken t
        SET t.usedAt = CURRENT_TIMESTAMP
        WHERE t.user.id = :userId
          AND t.usedAt IS NULL
    """)
    int invalidarTokensAtivos(@Param("userId") UUID userId);
}