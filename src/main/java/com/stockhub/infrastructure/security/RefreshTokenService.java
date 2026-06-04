package com.stockhub.infrastructure.security;

import com.stockhub.domain.exception.InvalidTokenException;
import com.stockhub.domain.model.RefreshToken;
import com.stockhub.domain.model.Usuario;
import com.stockhub.domain.port.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repo;

    @Value("${jwt.refresh-token-days}")
    private long refreshTokenDays;

    @Transactional
    public RefreshToken create(Usuario usuario) {
        RefreshToken token = RefreshToken.builder()
                .usuario(usuario)
                .token(UUID.randomUUID().toString() + UUID.randomUUID().toString())
                .expiresAt(LocalDateTime.now().plusDays(refreshTokenDays))
                .revoked(false)
                .build();
        return repo.save(token);
    }

    @Transactional
    public RefreshToken rotate(String oldTokenValue) {
        RefreshToken old = validate(oldTokenValue);
        old.setRevoked(true);
        repo.save(old);
        return create(old.getUsuario());
    }

    @Transactional
    public void revoke(String tokenValue) {
        repo.findByToken(tokenValue).ifPresent(t -> {
            t.setRevoked(true);
            repo.save(t);
        });
    }

    public RefreshToken validate(String tokenValue) {
        RefreshToken t = repo.findByToken(tokenValue)
                .orElseThrow(() -> new InvalidTokenException("Refresh token no encontrado"));
        if (Boolean.TRUE.equals(t.getRevoked())) {
            throw new InvalidTokenException("Refresh token revocado");
        }
        if (t.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Refresh token expirado");
        }
        return t;
    }
}
