package com.railgo.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    private static final long EXPIRATION_TIME = 7L * 24 * 60 * 60 * 1000; // 7 days

    private final SecretKey secretKey;

    public JwtTokenProvider(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateGuestToken() {
        return buildToken("guest-" + UUID.randomUUID(), "GUEST", null);
    }

    public String generateUserToken(Long userId) {
        return buildToken(String.valueOf(userId), "USER", null);
    }

    public String generateUserToken(Long userId, String sessionId) {
        return buildToken(String.valueOf(userId), "USER", sessionId);
    }

    private String buildToken(String subject, String role, String sessionId) {
        Date now = new Date();
        var builder = Jwts.builder()
                .setSubject(subject)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + EXPIRATION_TIME))
                .signWith(secretKey, SignatureAlgorithm.HS256);

        if (sessionId != null) {
            builder.claim("sessionId", sessionId);
        }

        return builder.compact();
    }

    public Claims parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)   // throws if signature is bad or token expired
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Long getUserId(String token) {
        Claims claims = parse(token);
        if (!"USER".equals(claims.get("role", String.class))) {
            throw new JwtException("Not a logged-in user token");
        }
        return Long.valueOf(claims.getSubject());
    }

    public String getSessionId(String token) {
        try {
            Claims claims = parse(token);
            return claims.get("sessionId", String.class);
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    public boolean isLoggedInUser(String token) {
        try {
            return "USER".equals(parse(token).get("role", String.class));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
