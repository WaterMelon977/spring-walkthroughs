package com.example.social_login.controller;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for authenticated user endpoints.
 */
@RestController
@RequestMapping("/api")
public class UserController {

    /**
     * Returns the current authenticated user's information.
     * The email is extracted from the JWT by the JwtAuthenticationFilter
     * and set as the principal in the SecurityContext.
     *
     * @param authentication The authentication object from security context
     * @return Map containing the authenticated user's email
     */
    @GetMapping("/me")
    public Map<String, Object> getCurrentUser(Authentication authentication) {
        // The principal is set to the email string by JwtAuthenticationFilter
        String email = (String) authentication.getPrincipal();
        return Map.of("email", email);
    }
}
