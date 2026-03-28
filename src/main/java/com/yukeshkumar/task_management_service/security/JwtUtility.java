package com.yukeshkumar.task_management_service.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
public class JwtUtility {

    private final SecretKey key;
    private final long expiration;

    public JwtUtility(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.expiration}") long expiration) {

        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
    }
    private Claims extractClaims(String token) {


        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public UUID extractUserId(String token) {

        Claims claims = extractClaims(token);

        String userId = claims.getSubject();

        return UUID.fromString(userId);
    }

    public String extractRole(String token) {

        Claims claims = extractClaims(token);

        return claims.get("role", String.class);
    }

    public UserDetails extractUserDetails(String token) {
        Claims claims = extractClaims(token);
        UUID userId = UUID.fromString(claims.getSubject());
        String role = claims.get("role", String.class);
        return new UserDetails(userId, role);
    }

    public static class UserDetails {
        private final UUID userId;
        private final String role;

        public UserDetails(UUID userId, String role) {
            this.userId = userId;
            this.role = role;
        }

        public UUID getUserId() {
            return userId;
        }

        public String getRole() {
            return role;
        }
    }

    public boolean validateToken(String token) {

        try {
            extractClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
