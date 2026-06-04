package com.stockhub.unit.security;

import com.stockhub.domain.exception.InvalidTokenException;
import com.stockhub.domain.model.RefreshToken;
import com.stockhub.domain.model.Usuario;
import com.stockhub.domain.port.RefreshTokenRepository;
import com.stockhub.infrastructure.security.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock private RefreshTokenRepository repo;

    @InjectMocks private RefreshTokenService service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "refreshTokenDays", 7L);
    }

    @Test
    void create_persistsTokenWithFutureExpiry() {
        Usuario user = usuario();
        when(repo.save(any(RefreshToken.class))).thenAnswer(inv -> inv.getArgument(0));

        RefreshToken created = service.create(user);

        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(repo).save(captor.capture());
        RefreshToken saved = captor.getValue();
        assertThat(saved.getUsuario()).isSameAs(user);
        assertThat(saved.getRevoked()).isFalse();
        assertThat(saved.getExpiresAt()).isAfter(LocalDateTime.now().plusDays(6));
        assertThat(created.getToken()).isEqualTo(saved.getToken());
    }

    @Test
    void validate_throws_whenNotFound() {
        when(repo.findByToken("X")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.validate("X"))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void validate_throws_whenRevoked() {
        RefreshToken t = tokenWithExpiry(LocalDateTime.now().plusDays(1));
        t.setRevoked(true);
        when(repo.findByToken("X")).thenReturn(Optional.of(t));

        assertThatThrownBy(() -> service.validate("X"))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("revocado");
    }

    @Test
    void validate_throws_whenExpired() {
        RefreshToken t = tokenWithExpiry(LocalDateTime.now().minusMinutes(1));
        when(repo.findByToken("X")).thenReturn(Optional.of(t));

        assertThatThrownBy(() -> service.validate("X"))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("expirado");
    }

    @Test
    void rotate_revokesOldAndCreatesNew() {
        RefreshToken old = tokenWithExpiry(LocalDateTime.now().plusDays(1));
        when(repo.findByToken("OLD")).thenReturn(Optional.of(old));
        when(repo.save(any(RefreshToken.class))).thenAnswer(inv -> inv.getArgument(0));

        RefreshToken nuevo = service.rotate("OLD");

        assertThat(old.getRevoked()).isTrue();
        assertThat(nuevo.getToken()).isNotEqualTo("OLD");
        assertThat(nuevo.getUsuario()).isSameAs(old.getUsuario());
    }

    private static Usuario usuario() {
        Usuario u = new Usuario();
        u.setId(1L);
        u.setEmail("a@b.com");
        return u;
    }

    private static RefreshToken tokenWithExpiry(LocalDateTime expiry) {
        RefreshToken t = RefreshToken.builder()
                .token("OLD")
                .usuario(usuario())
                .expiresAt(expiry)
                .revoked(false)
                .build();
        return t;
    }
}
