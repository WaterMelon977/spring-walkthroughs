package com.example.social_login.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean // Defines a Spring Bean that will be managed by the Spring container
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Creates and configures the main SecurityFilterChain (replaces older
        // WebSecurityConfigurerAdapter)

        http // Starts configuration on the HttpSecurity object
                .authorizeHttpRequests(auth -> auth // Configures authorization rules for HTTP requests
                        .requestMatchers("/", "/public").permitAll() // Allows unauthenticated access to root ("/") and
                                                                     // "/public" paths
                        .anyRequest().authenticated()) // Requires authentication for all other requests

                .oauth2Login(Customizer.withDefaults()) // Enables OAuth2 Login (social login) with default settings
                                                        // (login page, success handling, etc.)
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID"));

        return http.build(); // Builds and returns the configured SecurityFilterChain bean
    }

}
