package com.railgo.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Set token validity (e.g., 24 hours in milliseconds)
    private static final long EXPIRATION_TIME = 86400000;

    public String generateGuestToken() {
        return Jwts.builder()
                .setSubject("GUEST_USER") // Identify this as an unauthenticated guest
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }
}
