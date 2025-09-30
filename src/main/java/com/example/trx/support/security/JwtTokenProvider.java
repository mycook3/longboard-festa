package com.example.trx.support.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    private final Key signingKey;
    private final long validityInMillis;

    public JwtTokenProvider(
        @Value("${security.jwt.secret:change-me-change-me-change-me-123456}") String secret,
        @Value("${security.jwt.token-validity-in-seconds:3600}") long validityInSeconds
    ) {
        byte[] keyBytes = resolveKeyBytes(secret);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.validityInMillis = validityInSeconds * 1000;
    }

    public String generateToken(String subject, List<String> roles) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusMillis(validityInMillis);
        return Jwts.builder()
            .setSubject(subject)
            .claim("roles", roles)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(expiresAt))
            .signWith(signingKey, SignatureAlgorithm.HS256)
            .compact();
    }

    private byte[] resolveKeyBytes(String secret) {
        if (secret == null) {
            return new byte[0];
        }
        try {
            return Decoders.BASE64.decode(secret);
        } catch (IllegalArgumentException ex) {
            return secret.getBytes(StandardCharsets.UTF_8);
        }
    }
}
