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
        
        // Allow all origins during development
        config.addAllowedOrigin("*");
        // or specific origins:
        // config.addAllowedOrigin("http://localhost:4200");
        // config.addAllowedOrigin("http://localhost:9000");
        
        config.addAllowedMethod("*"); // Allow all HTTP methods
        config.addAllowedHeader("*"); // Allow all headers
        config.setAllowCredentials(false); // Must be false if using allowedOrigin="*"
        
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}