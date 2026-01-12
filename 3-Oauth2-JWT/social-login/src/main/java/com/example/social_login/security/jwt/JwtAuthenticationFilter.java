package com.example.social_login.security.jwt;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.social_login.config.JwtProperties;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filter that authenticates requests based on JWT stored in HttpOnly cookie.
 * Runs once per request, extracting and validating the JWT before setting
 * the security context.
 * 
 * Note: This filter does NOT check Authorization header - JWTs are only
 * accepted from cookies to prevent XSS token theft.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    public JwtAuthenticationFilter(JwtService jwtService, JwtProperties jwtProperties) {
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Extract JWT from cookie only (no Authorization header)
        Optional<String> tokenOpt = CookieUtils.getCookieValue(request, jwtProperties.cookieName());

        if (tokenOpt.isPresent()) {
            String token = tokenOpt.get();

            // Validate token signature and expiration
            if (jwtService.isTokenValid(token)) {
                try {
                    String email = jwtService.extractEmail(token);
                    Authentication auth = new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            List.of() // No authorities needed for basic auth
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } catch (Exception e) {
                    // Token parsing failed, clear any existing authentication
                    SecurityContextHolder.clearContext();
                }
            } else {
                // Invalid or expired token, ensure no authentication is set
                SecurityContextHolder.clearContext();
            }
        }
        // No token present - continue without authentication (let security config
        // handle access)

        filterChain.doFilter(request, response);
    }
}
