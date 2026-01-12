package com.example.social_login.controller;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.social_login.config.JwtProperties;
import com.example.social_login.security.jwt.CookieUtils;

/**
 * Controller for authentication-related endpoints.
 */
@RestController
public class AuthController {

    private final JwtProperties jwtProperties;

    public AuthController(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    /**
     * Logs out the user by clearing the JWT cookie.
     * No session invalidation needed (stateless architecture).
     *
     * @return Success response with Set-Cookie header to clear the JWT
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        // Build a cookie with maxAge=0 to clear it
        var clearCookie = CookieUtils.buildJwtCookie("", jwtProperties, true);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clearCookie.toString())
                .body(Map.of(
                        "message", "Logged out successfully",
                        "status", "success"));
    }
}
