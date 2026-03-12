package com.davidcastel.services_saas.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    // Clave secreta en Base64.
    // Es solo de ejemplo. Más adelante la moveremos a application.yml
    private static final String SECRET_KEY =
            "ZG9ub3R1c2V0aGlzaW5wcm9kdWN0aW9uZG9ub3R1c2VpdA==";

    // Convierte la clave Base64 en una SecretKey que usa JJWT para firmar/verificar
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Genera un token con:
    // - subject = username
    // - fecha de emisión
    // - fecha de expiración
    // - firma digital con la clave secreta
    public String generateToken(String username) {
        long now = System.currentTimeMillis();
        long expiration = now + 1000 * 60 * 60; // 1 hora

        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(now))
                .expiration(new Date(expiration))
                .signWith(getSigningKey())
                .compact();
    }

    // Extrae el username del token
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // Comprueba:
    // 1) que el username del token coincide
    // 2) que no esté expirado
    public boolean isTokenValid(String token, String username) {
        String extractedUsername = extractUsername(token);
        return extractedUsername.equals(username) && !isTokenExpired(token);
    }

    // Comprueba si ha expirado
    private boolean isTokenExpired(String token) {
        Date expiration = extractAllClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    // Parsea el token y obtiene todos sus claims
    // En JJWT 0.12.x se usa parser().verifyWith(key).build().parseSignedClaims(...)
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}