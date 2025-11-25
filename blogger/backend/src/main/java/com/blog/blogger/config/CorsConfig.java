package com.blog.blogger.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Allow specific origins with credentials (needed for JWT authentication)
        config.addAllowedOriginPattern("http://localhost:4200"); // Frontend
        config.addAllowedOriginPattern("http://localhost:8080"); // Backend

        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("PATCH");

        config.addAllowedHeader("*"); // Allow all headers (including Authorization)
        config.setAllowCredentials(true); // Required for sending JWT tokens
        config.setMaxAge(3600L); // Cache preflight response for 1 hour

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}