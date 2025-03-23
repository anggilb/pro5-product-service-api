package com.example.product_service_api.services;

import com.example.product_service_api.commons.dtos.TokenResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
@Slf4j
public class JwtService {
    private final Key secretKey;
    private static final String ERROR_EXPIRED_TOKEN = "Token has expired";
    private static final String ERROR_INVALID_TOKEN = "Invalid token signature";
    private static final String ERROR_USER_ID_CONVERSION = "Error converting user ID to Long";

    public JwtService(@Value("${jwt.secret}") String secretToken) {
        this.secretKey = Keys.hmacShaKeyFor(secretToken.getBytes(StandardCharsets.UTF_8));
    }

    public TokenResponse generateToken(Long userId) {
        Date expirationDate = new Date(System.currentTimeMillis() + 86400000); // Token válido por 24 horas
        try {
            String token = Jwts.builder()
                    .setSubject(String.valueOf(userId))
                    .setIssuedAt(new Date())
                    .setExpiration(expirationDate)
                    .signWith(secretKey, SignatureAlgorithm.HS256)
                    .compact();
            log.info("Generated JWT for user ID: {}", userId);
            return TokenResponse.builder()
                    .accessToken(token)
                    .build();
        } catch (JwtException e) {
            log.error("Failed to generate JWT: {}", e.getMessage());
            return null;
        }
    }

    public Claims getClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            log.error(ERROR_INVALID_TOKEN);
            return null;
        }
    }

    public boolean isExpired(String token) {
        Claims claims = getClaims(token);
        if (claims == null) return true;

        try {
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            log.error(ERROR_EXPIRED_TOKEN);
            return true;
        }
    }

    public Long extractedUserId(String token) {
        Claims claims = getClaims(token);
        if (claims == null) return null;

        try {
            String userIdString = claims.getSubject();
            Long userId = Long.valueOf(userIdString);
            log.info("Extracted user ID from JWT: {}", userId);
            return userId;
        } catch (NumberFormatException e) {
            log.error(ERROR_USER_ID_CONVERSION + ": {}", e.getMessage());
            return null;
        }
    }
}
