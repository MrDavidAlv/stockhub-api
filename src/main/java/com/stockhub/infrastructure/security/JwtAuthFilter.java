package com.stockhub.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain) throws ServletException, IOException {

        try {
            String header = request.getHeader("Authorization");
            if (header != null && header.startsWith(BEARER_PREFIX)
                    && SecurityContextHolder.getContext().getAuthentication() == null) {
                String token = header.substring(BEARER_PREFIX.length());
                if (jwtService.isValid(token)) {
                    authenticate(request, jwtService.extractEmail(token));
                }
            }
        } catch (Exception e) {
            // Un token valido cuyo usuario fue borrado, o cualquier fallo al
            // resolver el usuario, no debe romper la cadena con un 500: este
            // filtro corre antes del ExceptionTranslationFilter, asi que una
            // excepcion aqui se escaparia del GlobalExceptionHandler. Se limpia
            // el contexto y la peticion sigue sin autenticar; el
            // AuthenticationEntryPoint respondera 401 de forma consistente.
            SecurityContextHolder.clearContext();
            logger.warn("Autenticacion JWT descartada: " + e.getMessage());
        }
        chain.doFilter(request, response);
    }

    private void authenticate(HttpServletRequest request, String email) {
        UserDetails ud = userDetailsService.loadUserByUsername(email);
        // Un usuario desactivado (activo=false) no debe quedar autenticado aunque
        // su token siga vigente. El DaoAuthenticationProvider solo valida esto en
        // el login; en el flujo stateless hay que verificarlo en cada peticion.
        if (!ud.isEnabled()) {
            return;
        }
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                ud, null, ud.getAuthorities());
        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
