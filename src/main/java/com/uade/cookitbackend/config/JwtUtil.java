package com.uade.cookitbackend.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;
@Component
public class JwtUtil {

    /**
     * Debe ser una cadena Base64 de al menos 256 bits (32 bytes).
     * Generála con Keys.secretKeyFor(HS256) + Encoders.BASE64.
     */
    @Value("${jwt.secret}")
    private String secret;

    /** Tiempo de expiración en milisegundos */
    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    /** Construye la Key HMAC-SHA256 a partir del secreto Base64 */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Genera un JWT con claim "userId" y "sub"=subject (usualmente el mail).
     */
    public String generateToken(Integer userId, String subject) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(subject)
                .claim("userId", userId)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /** Extrae el "sub" (subject) del token */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /** Extrae el claim "userId" como Integer, incluso si viene como String o Number */
    public Integer extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        Object userIdObj = claims.get("userId");
        if (userIdObj instanceof Number) {
            return ((Number) userIdObj).intValue();
        } else if (userIdObj != null) {
            return Integer.parseInt(userIdObj.toString());
        }
        return null;
    }

    /** Extrae cualquier claim usando un resolver funcional */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /** Parsea y valida internamente la firma, lanza excepción si no es válido */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /** Comprueba que el token esté bien formado, la firma sea válida y no esté expirado */
    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}

