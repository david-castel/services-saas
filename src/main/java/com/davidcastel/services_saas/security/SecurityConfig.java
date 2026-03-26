package com.davidcastel.services_saas.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración de seguridad para la aplicación.
 * Aquí se definen las siguientes reglas globales de seguridad:
 *  - Qué rutas son públicas
 *  - Qué rutas requieren autenticación
 *  - Que no haya sesión
 *  - Que se use nuestro filtro JWT
 *
 *  Intercepta la request (cada petición) antes de llegar al controller.
 */
@Configuration
@EnableMethodSecurity
/**
 * @EnableMethodSecurity:
 *  Permite usar anotaciones como:
 *      @PreAuthorize("hasRole('ADMIN')")
 * en controllers o services.
 */
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        System.out.println(">>> SecurityConfig constructor");
        System.out.println(">>> JwtAuthenticationFilter class injected: " + jwtAuthenticationFilter.getClass().getName());

    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println(">>> Building SecurityFilterChain");
        System.out.println(">>> Adding filter before UsernamePasswordAuthenticationFilter: "
                + jwtAuthenticationFilter.getClass().getName());

        http
                // En APIs REST con JWT solemos desactivar CSRF:
                // CSRF protege aplicaciones con sesión y cookies.
                // Una API REST con JWT, donde:
                //  - no hay sesión
                //  - no hay cookies
                //  - cada request lleva token
                // Por eso se desactiva.
                .csrf(csrf -> csrf.disable())

                // No queremos sesión: cada request debe venir con su token.
                // Le dice a Spring Security:
                //      - no crees sesión
                //      - no uses sesión para recordar autenticación
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Reglas de autorización:
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/**",
                                "/api/health",

                                // Swagger / OpenAPI
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                // Manejo de excepciones:
                //
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
                        })
                )

                // Esto no es necesario para JWT puro, pero no molesta en esta fase
//                .httpBasic(Customizer.withDefaults())

                // Añadimos nuestro filtro antes del filtro estándar de username/password
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    /**
     * Es el componente que:
     *      - hashea passwords
     *      - compara password plana con hash almacenado
     * Usaremos BCryptPasswordEncoder, que es la opción estándar.
     *
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager:
     *      Es el punto de entrada de autenticación de Spring.
     *      Es el motor que autentica usuarios.
     * Cuando tú llames a:
     *      authenticationManager.authenticate(...)
     *
     *  Spring hará esto por debajo:
     *      - usará tu UserDetailsServiceImpl
     *      - cargará el usuario por email
     *      - usará el PasswordEncoder
     *      - validará credenciales
     *
     * @param authenticationConfiguration
     * @return
     * @throws Exception
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}