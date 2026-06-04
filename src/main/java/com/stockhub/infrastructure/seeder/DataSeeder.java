package com.stockhub.infrastructure.seeder;

import com.stockhub.domain.model.Rol;
import com.stockhub.domain.model.Usuario;
import com.stockhub.domain.port.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${seed.admin-email}")
    private String adminEmail;

    @Value("${seed.admin-password}")
    private String adminPassword;

    @Value("${seed.externo-email}")
    private String externoEmail;

    @Value("${seed.externo-password}")
    private String externoPassword;

    @Override
    public void run(String... args) {
        seedIfMissing(adminEmail, adminPassword, "Administrador", Rol.ADMIN);
        seedIfMissing(externoEmail, externoPassword, "Usuario Externo", Rol.EXTERNO);
    }

    private void seedIfMissing(String email, String rawPassword, String nombre, Rol rol) {
        if (usuarioRepository.findByEmail(email).isPresent()) {
            return;
        }
        Usuario u = Usuario.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .nombre(nombre)
                .rol(rol)
                .activo(true)
                .build();
        usuarioRepository.save(u);
        log.info("Usuario seed creado: {} ({})", email, rol);
    }
}
