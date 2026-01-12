package com.example.social_login.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for public endpoints that don't require authentication.
 */
@RestController
@RequestMapping("/api")
public class PublicController {

    @GetMapping("/public")
    public Map<String, String> publicEndpoint() {
        return Map.of("message", "This is a public endpoint");
    }
}
