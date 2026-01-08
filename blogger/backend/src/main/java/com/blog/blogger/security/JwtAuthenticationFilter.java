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

        String authHeader = request.getHeader("Authorization");
        logger.info("Authorization header: " + (authHeader != null ? "Present" : "Missing"));

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("No valid Authorization header found for " + requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = authHeader.substring(7);

            String username = jwtUtil.extractUsername(token);
            logger.info("Token username: " + username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                logger.info("User details loaded for: " + username);

                if (userDetails == null) {
                    logger.error("UserDetails is null for username: " + username);
                    filterChain.doFilter(request, response);
                    return;
                }

                if (jwtUtil.validateToken(token, userDetails.getUsername())) {
                    logger.info("Token validated successfully for: " + username);

                    com.blog.blogger.models.User user = userDetailsService.getUserByUsername(username);
                    logger.info("User entity loaded: " + user.getId());

                    UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                            user,  
                            null,  
                            userDetails.getAuthorities()  
                        );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.info("Authentication set in SecurityContext for: " + username);
                } else {
                    logger.error("Token validation failed for: " + username);
                }
            }
        } catch (org.springframework.security.core.userdetails.UsernameNotFoundException e) {
            logger.error("JWT Authentication failed - User banned or not found: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\", \"banned\": true}");
            return; 
        } catch (Exception e) {
            
            logger.error("JWT Authentication failed: " + e.getMessage(), e);
        }

       
        filterChain.doFilter(request, response);
    }
}
