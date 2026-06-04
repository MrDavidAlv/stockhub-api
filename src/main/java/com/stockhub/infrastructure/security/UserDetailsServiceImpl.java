package com.stockhub.infrastructure.security;

import com.stockhub.domain.model.Usuario;
import com.stockhub.domain.port.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario u = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));
        return User.builder()
                .username(u.getEmail())
                .password(u.getPasswordHash())
                .disabled(!Boolean.TRUE.equals(u.getActivo()))
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + u.getRol().name())))
                .build();
    }
}
