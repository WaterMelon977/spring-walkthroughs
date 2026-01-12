package com.example.social_login.security.jwt;

import java.util.Optional;

import org.springframework.http.ResponseCookie;

import com.example.social_login.config.JwtProperties;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Utility class for cookie operations related to JWT authentication.
 * Provides consistent cookie handling across the application.
 */
public final class CookieUtils {

    private CookieUtils() {
        // Utility class, prevent instantiation
    }

    /**
     * Extracts a cookie value from the request by name.
     *
     * @param request    The HTTP servlet request
     * @param cookieName The name of the cookie to extract
     * @return Optional containing the cookie value if found
     */
    public static Optional<String> getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }

        for (Cookie cookie : cookies) {
            if (cookieName.equals(cookie.getName())) {
                String value = cookie.getValue();
                if (value != null && !value.isBlank()) {
                    return Optional.of(value);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Builds a JWT cookie with secure settings.
     *
     * @param token      The JWT token value (empty string to clear cookie)
     * @param properties JWT configuration properties
     * @param clear      If true, creates a cookie with maxAge=0 to clear it
     * @return A ResponseCookie configured for secure JWT storage
     */
    public static ResponseCookie buildJwtCookie(String token, JwtProperties properties, boolean clear) {
        return ResponseCookie.from(properties.cookieName(), clear ? "" : token)
                .httpOnly(true) // Prevent JavaScript access (XSS protection)
                .secure(false) // Set to true in production with HTTPS
                .sameSite("Lax") // CSRF protection for cross-site requests
                .path("/") // Available for all paths
                .maxAge(clear ? 0 : properties.expirationMs() / 1000) // Convert ms to seconds
                .build();
    }
}
