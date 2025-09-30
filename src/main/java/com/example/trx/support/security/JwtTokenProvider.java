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
import org.springframework.util.StringUtils;

@Component
public class JwtTokenProvider {

    private static final String DEFAULT_SECRET =
        "ZmVzbWFuLXJhbXBtdXMtcmVzdC1qd3QtdG9rZW4tTURiVkR2Rnh2S2pS"; // base64 encoded 256-bit key

    private final Key signingKey;
    private final long validityInMillis;

    public JwtTokenProvider(
        @Value("${SECURITY_JWT_SECRET:}") String secret,
        @Value("${SECURITY_JWT_TOKEN_VALIDITY_IN_SECONDS:3600}") long validityInSeconds
    ) {
        String resolvedSecret = StringUtils.hasText(secret) ? secret : DEFAULT_SECRET;
        byte[] keyBytes = resolveKeyBytes(resolvedSecret);
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
        if (!StringUtils.hasText(secret)) {
            throw new IllegalStateException("JWT 비밀 키가 설정되지 않았습니다.");
        }
        try {
            return Decoders.BASE64.decode(secret);
        } catch (IllegalArgumentException ex) {
            byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
            if (keyBytes.length < 32) {
                throw new IllegalStateException("JWT 비밀 키는 최소 32바이트 이상이어야 합니다.");
            }
            return keyBytes;
        }
    }
}
