package com.example.social_login;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.example.social_login.config.AppProperties;
import com.example.social_login.config.JwtProperties;

@SpringBootApplication
@EnableConfigurationProperties({ JwtProperties.class, AppProperties.class })
public class SocialLoginApplication {

	public static void main(String[] args) {
		SpringApplication.run(SocialLoginApplication.class, args);
	}

}
