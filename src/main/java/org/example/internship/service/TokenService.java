package org.example.internship.service;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.example.internship.config.properties.TokenProperties;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {
    private final TokenProperties tokenProperties;
    private SecretKey key;


    public String generateAccessToken(Authentication authentication) {
        String username = authentication.getName();
        Date exp = new Date(System.currentTimeMillis() + tokenProperties.getAccessExpiration() * 1000L);
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(exp)
                .claim("token_type", "access")
                .claim("role", authentication.getAuthorities())
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(Authentication authentication) {
        String username = authentication.getName();
        Date exp = new Date(System.currentTimeMillis() + tokenProperties.getRefreshExpiration() * 1000L);
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(exp)
                .claim("token_type", "refresh")
                .claim("role", authentication.getAuthorities())
                .signWith(key)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    public String getTokenType(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("token_type", String.class);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error(e.getMessage());
        }
        return false;
    }

    @SneakyThrows
    @PostConstruct
    public void init() {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
        key = keyGenerator.generateKey();
    }
}
