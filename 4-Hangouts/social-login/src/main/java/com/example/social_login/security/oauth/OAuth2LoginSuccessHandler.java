package com.example.social_login.security.oauth;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.social_login.config.AppProperties;
import com.example.social_login.config.JwtProperties;
import com.example.social_login.security.jwt.CookieUtils;
import com.example.social_login.security.jwt.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles successful OAuth2 authentication by issuing a JWT token.
 * The JWT is stored in an HttpOnly cookie and the user is redirected
 * to the frontend application.
 */
@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final AppProperties appProperties;

    public OAuth2LoginSuccessHandler(
            JwtService jwtService,
            JwtProperties jwtProperties,
            AppProperties appProperties) {
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
        this.appProperties = appProperties;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");

        // Generate JWT token for the authenticated user
        String jwt = jwtService.generateToken(email);

        // Build secure HttpOnly cookie using centralized utility
        var cookie = CookieUtils.buildJwtCookie(jwt, jwtProperties, false);

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // Redirect to frontend (no token in URL)
        response.sendRedirect(appProperties.frontendUrl());
    }
}
