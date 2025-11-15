package com.blog.blogger.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * JwtUtil - Handles JWT token generation and validation
 *
 * This class is responsible for:
 * 1. Creating JWT tokens when user logs in
 * 2. Validating JWT tokens on each request
 * 3. Extracting username from tokens
 */
@Component
public class JwtUtil {

    // Secret key from application.properties - used to sign tokens
    @Value("${jwt.secret}")
    private String secretString;

    // Token expiration time from application.properties (in milliseconds)
    @Value("${jwt.expiration}")
    private long expirationTime;

    /**
     * Generate a secret key from the secret string
     * This key is used to sign and verify JWT tokens
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretString.getBytes());
    }

    /**
     * Generate a JWT token for a user
     *
     * @param username - The user's username
     * @return JWT token as a String
     *
     * Token contains:
     * - Subject (username)
     * - Issued at time (when created)
     * - Expiration time (when it expires)
     * - Signature (to verify it's authentic)
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)                              // Who the token is for
                .issuedAt(new Date())                           // When it was created
                .expiration(new Date(System.currentTimeMillis() + expirationTime)) // When it expires
                .signWith(getSigningKey())                      // Sign it with our secret
                .compact();                                     // Build the token
    }

    /**
     * Extract username from JWT token
     *
     * @param token - The JWT token
     * @return The username stored in the token
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Extract all claims (data) from JWT token
     * Claims are the payload of the JWT (username, expiration, etc.)
     *
     * @param token - The JWT token
     * @return Claims object containing all token data
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())    // Verify signature
                .build()
                .parseSignedClaims(token)       // Parse the token
                .getPayload();                  // Get the data
    }

    /**
     * Check if token is expired
     *
     * @param token - The JWT token
     * @return true if expired, false if still valid
     */
    private boolean isTokenExpired(String token) {
        Date expiration = extractAllClaims(token).getExpiration();
        return expiration.before(new Date()); // Is expiration date in the past?
    }

    /**
     * Validate JWT token
     * Checks if:
     * 1. The username matches
     * 2. The token is not expired
     *
     * @param token - The JWT token
     * @param username - The username to validate against
     * @return true if valid, false otherwise
     */
    public boolean validateToken(String token, String username) {
        String tokenUsername = extractUsername(token);
        return (tokenUsername.equals(username) && !isTokenExpired(token));
    }
}
