package com.example.social_login.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Application-level configuration properties.
 * 
 * @param frontendUrl The URL of the frontend application for OAuth2 redirects
 */
@ConfigurationProperties(prefix = "application")
public record AppProperties(String frontendUrl) {

    /**
     * Default values for optional properties.
     */
    public AppProperties {
        if (frontendUrl == null || frontendUrl.isBlank()) {
            frontendUrl = "http://localhost:3000";
        }
    }
}
