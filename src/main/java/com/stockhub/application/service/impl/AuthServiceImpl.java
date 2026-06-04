package com.stockhub.application.service.impl;

import com.stockhub.application.dto.LoginRequest;
import com.stockhub.application.dto.TokenPairResponse;
import com.stockhub.application.service.AuthService;
import com.stockhub.domain.exception.InvalidCredentialsException;
import com.stockhub.domain.model.RefreshToken;
import com.stockhub.domain.model.Usuario;
import com.stockhub.domain.port.UsuarioRepository;
import com.stockhub.infrastructure.security.JwtService;
import com.stockhub.infrastructure.security.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Override
    public TokenPairResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException();
        }
        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);
        return buildNewPair(usuario);
    }

    @Override
    public TokenPairResponse refresh(String refreshToken) {
        RefreshToken rotated = refreshTokenService.rotate(refreshToken);
        return buildAccessFor(rotated.getUsuario(), rotated.getToken());
    }

    @Override
    public void logout(String refreshToken) {
        refreshTokenService.revoke(refreshToken);
    }

    private TokenPairResponse buildNewPair(Usuario usuario) {
        RefreshToken refresh = refreshTokenService.create(usuario);
        return buildAccessFor(usuario, refresh.getToken());
    }

    private TokenPairResponse buildAccessFor(Usuario usuario, String refreshTokenValue) {
        String access = jwtService.generateAccessToken(usuario.getEmail(), usuario.getRol());
        return new TokenPairResponse(
                access,
                refreshTokenValue,
                usuario.getRol().name(),
                usuario.getNombre(),
                usuario.getEmail());
    }
}
