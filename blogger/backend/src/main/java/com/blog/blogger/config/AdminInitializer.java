package com.blog.blogger.config;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.blog.blogger.models.Role;
import com.blog.blogger.models.User;  // ← ADD THIS IMPORT
import com.blog.blogger.repository.UserRepository;

@Configuration
public class AdminInitializer {
    
    @Value("${ADMIN_USERNAME:admin}")
    private String adminUsername;

    @Value("${ADMIN_EMAIL:admin@blog.com}")
    private String adminEmail;

    @Value("${ADMIN_PASSWORD:Admin@123}")
    private String adminPassword;

    @Bean
    CommandLineRunner initAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Check if admin already exists
            if (userRepository.findByUsername(adminUsername).isEmpty()) {
                User admin = new User();
                admin.setUsername(adminUsername);
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setRole(Role.ADMIN);  // ← CHANGED FROM "ADMIN" to Role.ADMIN
                admin.setFullName("System Administrator");
                admin.setBio("Default administrator account");
                admin.setIsBanned(false);
                admin.setCreatedAt(LocalDateTime.now());
                admin.setUpdatedAt(LocalDateTime.now());
                
                userRepository.save(admin);
                
                System.out.println("========================================");
                System.out.println("✅ ADMIN USER CREATED SUCCESSFULLY!");
                System.out.println("========================================");
                System.out.println("   Username: " + adminUsername);
                System.out.println("   Email:    " + adminEmail);
                System.out.println("   Password: " + adminPassword);
                System.out.println("========================================");
                System.out.println("⚠️  IMPORTANT: Change password after first login!");
                System.out.println("========================================");
            } else {
                System.out.println("ℹ️  Admin user '" + adminUsername + "' already exists. Skipping creation.");
            }
        };
    }
}