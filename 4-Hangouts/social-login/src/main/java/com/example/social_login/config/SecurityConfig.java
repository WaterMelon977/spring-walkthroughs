package com.example.social_login.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.social_login.security.jwt.JwtAuthenticationFilter;
import com.example.social_login.security.oauth.OAuth2LoginSuccessHandler;

/**
 * Main security configuration for the application.
 * Configures stateless JWT authentication with OAuth2 social login.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;
        private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
        private final AppProperties appProperties;

        public SecurityConfig(
                        JwtAuthenticationFilter jwtAuthenticationFilter,
                        OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler,
                        AppProperties appProperties) {
                this.jwtAuthenticationFilter = jwtAuthenticationFilter;
                this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
                this.appProperties = appProperties;
        }

        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                // CSRF: Disable for stateless JWT with HttpOnly cookies and SameSite=Lax
                                // SameSite=Lax in cookies provides CSRF protection for same-site requests
                                .csrf(csrf -> csrf.disable())

                                // Disable default logout to allow custom controller to handle /logout
                                .logout(logout -> logout.disable())

                                // CORS: Allow frontend origin with credentials
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                                // Stateless session: No server-side session storage
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                // Authorization rules
                                .authorizeHttpRequests(auth -> auth
                                                // Public endpoints - no authentication required
                                                .requestMatchers("/", "/api/public", "/oauth2/**", "/login", "/logout")
                                                .permitAll()
                                                // Protected endpoints - authentication required
                                                .requestMatchers("/api/me").authenticated()
                                                // All other requests require authentication
                                                .anyRequest().authenticated())

                                // Return 403 for unauthenticated requests (API behavior)
                                .exceptionHandling(exceptions -> exceptions
                                                .authenticationEntryPoint(new Http403ForbiddenEntryPoint()))

                                // OAuth2 login configuration with custom success handler
                                .oauth2Login(oauth2 -> oauth2
                                                .successHandler(oAuth2LoginSuccessHandler))

                                // Add JWT filter before username/password authentication filter
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration(); // Create CORS rules container

                configuration.setAllowedOrigins(
                                List.of(appProperties.frontendUrl())); // Allow requests only from frontend origin

                configuration.setAllowedMethods(
                                Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Allow common HTTP methods

                configuration.setAllowedHeaders(
                                List.of("*")); // Allow all request headers

                configuration.setAllowCredentials(true); // Allow cookies/auth headers in cross-origin requests

                configuration.setExposedHeaders(
                                List.of("Set-Cookie")); // Allow frontend to see Set-Cookie response header

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(); // Map CORS rules to URL
                                                                                                // paths

                source.registerCorsConfiguration(
                                "/**", configuration); // Apply CORS rules to all endpoints

                return source; // Expose configuration to Spring Security
        }

}
