package com.example.social_login.Controller;

import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String publicPage() {
        return "Public page";
    }

    @GetMapping("/secure")
    public Map<String, Object> securePage(@AuthenticationPrincipal OAuth2User user) {
        return user.getAttributes();

    }
}
