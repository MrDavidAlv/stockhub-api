package com.stockhub.infrastructure.security;

import com.stockhub.domain.model.Rol;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long accessTokenMillis;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-minutes}") long accessTokenMinutes) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenMillis = accessTokenMinutes * 60_000L;
    }

    public String generateAccessToken(String email, Rol rol) {
        Date now = new Date();
        return Jwts.builder()
                .subject(email)
                .claim("rol", rol.name())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + accessTokenMillis))
                .signWith(signingKey)
                .compact();
    }

    public String extractEmail(String token) {
        return parse(token).getSubject();
    }

    public boolean isValid(String token) {
        try {
            parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
