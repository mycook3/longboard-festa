package com.example.trx.support.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        List<String> roles = getRoles(claims);
        return new UsernamePasswordAuthenticationToken(
            claims.getSubject(),
            token,
            roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
        );
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException ex) {
            return false;
        } catch (ExpiredJwtException ex) {
            return false;
        } catch (UnsupportedJwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        Jws<Claims> claimsJws = Jwts.parserBuilder()
            .setSigningKey(signingKey)
            .build()
            .parseClaimsJws(token);
        return claimsJws.getBody();
    }

    @SuppressWarnings("unchecked")
    private List<String> getRoles(Claims claims) {
        Object roles = claims.get("roles");
        if (roles instanceof List<?>) {
            return ((List<?>) roles).stream()
                .filter(Objects::nonNull)
                .map(Object::toString)
                .collect(Collectors.toList());
        }
        return Collections.emptyList();
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
