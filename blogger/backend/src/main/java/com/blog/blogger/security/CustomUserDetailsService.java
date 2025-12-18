package com.blog.blogger.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.blog.blogger.models.User;
import com.blog.blogger.repository.UserRepository;

/**
 * CustomUserDetailsService - Loads user from database for Spring Security
 *
 * Spring Security needs to know:
 * "Hey, there's a user trying to log in. Can you get their info from database?"
 *
 * This service answers that question by:
 * 1. Taking a username/email
 * 2. Looking it up in the database
 * 3. Converting it to Spring Security's UserDetails format
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Load user by username (or email)
     *
     * This method is called by Spring Security to authenticate users
     *
     * @param username - Can be username or email
     * @return UserDetails - Spring Security's user format
     * @throws UsernameNotFoundException if user not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Try to find user by email first
        User user = userRepository.findByEmail(username)
                .orElseGet(() ->
                    // If not found by email, try username
                    userRepository.findByUsername(username)
                            .orElseThrow(() ->
                                new UsernameNotFoundException("User not found: " + username)
                            )
                );

        // Check if user is banned
        if (user.getIsBanned() != null && user.getIsBanned()) {
            throw new UsernameNotFoundException("User account is banned: " + username);
        }

        // Validate required fields
        if (user.getUsername() == null || user.getPassword() == null || user.getRole() == null) {
            throw new UsernameNotFoundException("User data is incomplete for: " + username);
        }

        // Convert our User entity to Spring Security's UserDetails
        // This tells Spring Security:
        // - What's the username?
        // - What's the password?
        // - What are the user's roles/permissions?
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())              // Username
                .password(user.getPassword())              // Hashed password
                .roles(user.getRole().name())              // Role (USER, ADMIN, etc.)
                .build();
    }

    /**
     * Get the actual User entity by username or email
     * This is used to get the full User object (not just UserDetails)
     *
     * @param username - Can be username or email
     * @return User entity
     * @throws UsernameNotFoundException if user not found or banned
     */
    public User getUserByUsername(String username) throws UsernameNotFoundException {
        // Try to find user by email first
        User user = userRepository.findByEmail(username)
                .orElseGet(() ->
                    // If not found by email, try username
                    userRepository.findByUsername(username)
                            .orElseThrow(() ->
                                new UsernameNotFoundException("User not found: " + username)
                            )
                );

        // Check if user is banned
        if (user.getIsBanned() != null && user.getIsBanned()) {
            throw new UsernameNotFoundException("User account is banned: " + username);
        }

        return user;
    }
}
