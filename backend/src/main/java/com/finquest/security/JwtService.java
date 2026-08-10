package com.finquest.security;

import com.finquest.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Generates, parses and validates the JWT access & refresh tokens.
 * <p>
 * The signing key, access-token lifetime and refresh-token lifetime are all
 * externalised to {@code application.properties}.
 */
@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration-ms}")
    private long accessExpirationMs;

    @Value("${app.jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    // ── Token generation ──────────────────────────────────────────────────────

    /** Builds an access token carrying the user id and role as claims. */
    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        claims.put("email", user.getEmail());
        return buildToken(claims, user.getId(), accessExpirationMs);
    }

    /** Builds a refresh token carrying only the user id (no role). */
    public String generateRefreshToken(User user) {
        return buildToken(new HashMap<>(), user.getId(), refreshExpirationMs);
    }

    private String buildToken(Map<String, Object> extraClaims, Long userId, long expirationMs) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(String.valueOf(userId))   // subject = user id
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ── Parsing / validation ──────────────────────────────────────────────────

    /** Extracts the user id (subject) from a token. */
    public Long extractUserId(String token) {
        return Long.valueOf(extractClaim(token, Claims::getSubject));
    }

    /** Extracts the role claim from an access token. */
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public boolean isTokenValid(String token) {
        try {
            return extractClaim(token, Claims::getExpiration).after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        final Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
