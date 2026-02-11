package com.gestiontests.security;

import com.gestiontests.entity.Administrateur;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;

public class JwtUtil {

    private static final String DEFAULT_SECRET = "CHANGE_ME_TO_A_LONG_RANDOM_SECRET_32_CHARS_MIN";

    private static Key getSigningKey() {
        String secret = System.getProperty("app.jwt.secret");
        if (secret == null || secret.trim().isEmpty()) {
            secret = System.getenv("APP_JWT_SECRET");
        }
        if (secret == null || secret.trim().isEmpty()) {
            secret = DEFAULT_SECRET;
        }

        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            byte[] padded = new byte[32];
            int len = Math.min(keyBytes.length, 32);
            System.arraycopy(keyBytes, 0, padded, 0, len);
            for (int i = len; i < 32; i++) {
                padded[i] = (byte) '0';
            }
            keyBytes = padded;
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }

    public static String generateAdminToken(Administrateur admin) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(8 * 60 * 60);

        return Jwts.builder()
            .setSubject(admin.getUsername())
            .claim("role", "admin")
            .claim("adminId", admin.getId())
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(exp))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    public static Claims validateAndGetClaims(String token) throws JwtException {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
}
