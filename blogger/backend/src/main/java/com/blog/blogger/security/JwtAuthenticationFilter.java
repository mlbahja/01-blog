package com.blog.blogger.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JwtAuthenticationFilter - Validates JWT tokens on every request
 *
 * This filter runs BEFORE Spring Security's authentication.
 * It checks if the request has a valid JWT token, and if so,
 * authenticates the user automatically.
 *
 * Flow:
 * 1. Extract JWT token from Authorization header
 * 2. Validate the token
 * 3. Load user details from database
 * 4. Set authentication in SecurityContext
 * 5. Continue with the request
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        logger.info("JWT Filter processing: " + request.getMethod() + " " + requestURI);

        // 1. Get Authorization header from request
        String authHeader = request.getHeader("Authorization");
        logger.info("Authorization header: " + (authHeader != null ? "Present" : "Missing"));

        // 2. Check if header exists and starts with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // No token found, continue without authentication
            logger.warn("No valid Authorization header found for " + requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 3. Extract token (remove "Bearer " prefix)
            String token = authHeader.substring(7);

            // 4. Extract username from token
            String username = jwtUtil.extractUsername(token);
            logger.info("Token username: " + username);

            // 5. If we have a username and user is not already authenticated
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // 6. Load user details from database for authentication validation
                // This will throw UsernameNotFoundException if user is banned
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                logger.info("User details loaded for: " + username);

                // Check if userDetails is null (defensive programming)
                if (userDetails == null) {
                    logger.error("UserDetails is null for username: " + username);
                    filterChain.doFilter(request, response);
                    return;
                }

                // 7. Validate token
                if (jwtUtil.validateToken(token, userDetails.getUsername())) {
                    logger.info("Token validated successfully for: " + username);

                    // 8. Get the actual User entity (not just UserDetails)
                    // This is needed for @AuthenticationPrincipal to work correctly
                    com.blog.blogger.models.User user = userDetailsService.getUserByUsername(username);
                    logger.info("User entity loaded: " + user.getId());

                    // 9. Create authentication token with User entity as principal
                    UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                            user,  // Use User entity as principal (not UserDetails)
                            null,  // credentials (password) - not needed after validation
                            userDetails.getAuthorities()  // user's roles/permissions
                        );

                    // 10. Set additional details (IP address, session ID, etc.)
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 11. Set authentication in SecurityContext
                    // This tells Spring Security: "This user is authenticated!"
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.info("Authentication set in SecurityContext for: " + username);
                } else {
                    logger.error("Token validation failed for: " + username);
                }
            }
        } catch (org.springframework.security.core.userdetails.UsernameNotFoundException e) {
            // User is banned or not found
            logger.error("JWT Authentication failed - User banned or not found: " + e.getMessage());
            // Return 403 Forbidden response
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\", \"banned\": true}");
            return; // Stop filter chain
        } catch (Exception e) {
            // Token is invalid, malformed, or expired
            // Log the error but continue without authentication
            logger.error("JWT Authentication failed: " + e.getMessage(), e);
        }

        // 11. Continue with the filter chain
        filterChain.doFilter(request, response);
    }
}
