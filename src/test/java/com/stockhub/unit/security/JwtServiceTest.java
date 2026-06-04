package com.stockhub.unit.security;

import com.stockhub.domain.model.Rol;
import com.stockhub.infrastructure.security.JwtService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private static final String SECRET = "unit-test-secret-with-at-least-32-characters-for-hs256-and-more";
    private final JwtService jwtService = new JwtService(SECRET, 60L);

    @Test
    void generateAccessToken_isParseable_andSubjectMatchesEmail() {
        String token = jwtService.generateAccessToken("admin@stockhub.local", Rol.ADMIN);

        assertThat(token).isNotBlank().contains(".");
        assertThat(jwtService.extractEmail(token)).isEqualTo("admin@stockhub.local");
        assertThat(jwtService.isValid(token)).isTrue();
    }

    @Test
    void isValid_returnsFalse_forMalformedToken() {
        assertThat(jwtService.isValid("no-soy-un-jwt")).isFalse();
    }

    @Test
    void isValid_returnsFalse_forNullToken() {
        assertThat(jwtService.isValid(null)).isFalse();
    }

    @Test
    void isValid_returnsFalse_forTokenSignedWithOtherSecret() {
        JwtService otro = new JwtService("otra-clave-distinta-larga-para-firmar-tokens-en-tests-32+", 60L);
        String token = otro.generateAccessToken("admin@stockhub.local", Rol.ADMIN);

        assertThat(jwtService.isValid(token)).isFalse();
    }
}
