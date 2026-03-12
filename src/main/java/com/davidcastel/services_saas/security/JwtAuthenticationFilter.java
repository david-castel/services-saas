package com.davidcastel.services_saas.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Intercepta la request antes de llegar al controller.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
        System.out.println(">>> JwtAuthenticationFilter bean created");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1) Leer cabecera Authorization
        final String authHeader = request.getHeader("Authorization");


        System.out.println(">>> URI: " + request.getRequestURI());
        System.out.println(">>> Authorization header: " + authHeader);


        // 2) Si no existe o no empieza por "Bearer ", no hacemos nada
        // y dejamos que la request continúe
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3) Extraer token quitando el prefijo "Bearer "
        String token = authHeader.substring(7);

        try {
            // 4) Sacar username del token
            String username = jwtService.extractUsername(token);

            System.out.println(">>> Username extraído del token: " + username);

            // 5) Si hay username y todavía no hay autenticación en el contexto
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // 6) Validar token
                if (jwtService.isTokenValid(token, username)) {

                    // 7) Crear objeto Authentication
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    username,
                                    null,
                                    AuthorityUtils.createAuthorityList("ROLE_USER")
                            );

                    // 8) Añadir detalles web de la request
                    authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // 9) Guardar autenticación en el SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    System.out.println(">>> Usuario autenticado correctamente con JWT");
                }
            }
        } catch (Exception ex) {
            // Si el token está mal, no autenticamos.
            // Spring Security ya bloqueará el acceso si la ruta requiere auth.

            System.out.println(">>> Error validando JWT: " + ex.getClass().getName());
            System.out.println(">>> Mensaje: " + ex.getMessage());
            ex.printStackTrace();
        }

        // 10) Seguir con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}