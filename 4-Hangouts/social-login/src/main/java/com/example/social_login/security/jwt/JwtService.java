package com.example.social_login.security.jwt;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import com.example.social_login.config.JwtProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Service responsible for JWT token generation and validation.
 * Centralizes all JWT operations for consistent security handling.
 */
@Service
public class JwtService {

    private final JwtProperties jwtProperties;
    private final SecretKey signingKey;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        // Generate signing key once at construction, not per-call
        this.signingKey = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes());
    }

    /**
     * Generates a JWT token for the given email/username.
     *
     * @param email The user's email (used as subject)
     * @return A signed JWT token string
     */
    public String generateToken(String email) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtProperties.expirationMs());

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(signingKey)
                .compact();
    }

    /**
     * Extracts the email/username from a valid JWT token.
     *
     * @param token The JWT token string
     * @return The subject (email) from the token
     * @throws JwtException if token is invalid or expired
     */
    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Validates the JWT token for proper signature and expiration.
     *
     * @param token The JWT token string
     * @return true if token is valid, false otherwise
     */
    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            // Token has expired
            return false;
        } catch (JwtException e) {
            // Invalid signature, malformed token, etc.
            return false;
        }
    }

    /**
     * Extracts all claims from the JWT token.
     *
     * @param token The JWT token string
     * @return The Claims object containing all token claims
     * @throws JwtException if token parsing fails
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
