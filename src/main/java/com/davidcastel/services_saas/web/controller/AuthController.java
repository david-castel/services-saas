package com.davidcastel.services_saas.web.controller;

import com.davidcastel.services_saas.security.JwtService;
import com.davidcastel.services_saas.web.dto.LoginRequest;
import com.davidcastel.services_saas.web.dto.LoginResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /**
     * Recibe usuario y contraseña, valida credenciales y devuelve el JWT.
     *
     * @param request
     * @return
     */
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {

//        // De momento usamos credenciales hardcodeadas
//        // Más adelante lo conectaremos a usuarios reales
//        if ("admin".equals(request.username()) &&
//                "admin".equals(request.password())) {
//
//            String token = jwtService.generateToken(request.username());
//            return new LoginResponse(token);
//        }
//
//        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");

        // Autenticamos el usuario y contraseña con el AuthenticationManager:
        // Qué pasa si no coincide? Lanza AuthenticationException (teóricamente devuelve un 401)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        String username = authentication.getName();
        String token = jwtService.generateToken(username);

        // Devolvemos el token JWT generado:
        return new LoginResponse(token);
    }
}