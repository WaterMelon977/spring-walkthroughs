package com.example.social_login.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JWT configuration properties externalized from application.yaml.
 * 
 * @param secret       The secret key used for signing JWTs (min 32 chars for
 *                     HS256)
 * @param expirationMs Token expiration time in milliseconds
 * @param cookieName   Name of the HttpOnly cookie storing the JWT
 */
@ConfigurationProperties(prefix = "application.security.jwt")
public record JwtProperties(
        String secret,
        long expirationMs,
        String cookieName) {

    /**
     * Default values for optional properties.
     */
    public JwtProperties {
        if (expirationMs <= 0) {
            expirationMs = 900000L; // 15 minutes default
        }
        if (cookieName == null || cookieName.isBlank()) {
            cookieName = "ACCESS_TOKEN";
        }
    }
}
