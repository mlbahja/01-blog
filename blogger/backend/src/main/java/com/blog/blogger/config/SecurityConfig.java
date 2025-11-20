package com.blog.blogger.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.blog.blogger.security.CustomUserDetailsService;
import com.blog.blogger.security.JwtAuthenticationFilter;

/**
 * SecurityConfig - Main Spring Security Configuration
 *
 * This class configures:
 * 1. Which endpoints are public (login, register) vs protected
 * 2. How authentication works (JWT-based, stateless)
 * 3. Password encoding (BCrypt)
 * 4. JWT filter integration
 * 5. Method-level security (@PreAuthorize, @Secured, etc.)
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, CustomUserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Configure the security filter chain
     * This defines which endpoints require authentication
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF (not needed for stateless JWT authentication)
            .csrf(csrf -> csrf.disable())

            // Configure endpoint authorization
            .authorizeHttpRequests(auth -> auth
                // Public endpoints - anyone can access (only login and register)
                .requestMatchers("/auth/register", "/auth/login").permitAll()
                .requestMatchers("/error").permitAll()

                // Allow public GET access to posts (viewing only)
                .requestMatchers(HttpMethod.GET, "/auth/posts", "/auth/posts/**").permitAll()

                // All other endpoints require authentication (including /auth/home)
                .anyRequest().authenticated()
            )

            // Use stateless session management (no server-side sessions)
            // Each request must contain a valid JWT token
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Set authentication provider
            .authenticationProvider(authenticationProvider())

            // Add JWT filter before Spring Security's authentication filter
            // This ensures JWT validation happens first
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Password encoder bean
     * Uses BCrypt hashing algorithm (industry standard)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Authentication provider
     * Tells Spring Security how to authenticate users
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Authentication manager bean
     * Used for manual authentication (e.g., during login)
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
