package com.example.ecommerce.auth.repository;

import com.example.ecommerce.auth.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    @Query("""
    SELECT rt FROM RefreshToken rt
    WHERE rt.user.username = :username
    AND rt.revoked = false
    """)
    List<RefreshToken> findActiveTokensByUserId(String username);

    Optional<RefreshToken> findByToken(String token);

}
