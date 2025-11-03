
package com.blog.blogger.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // نوقف CSRF
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/register", "/auth/login", "/css/**", "/js/**").permitAll() // نسمح بالـ endpoints
                .anyRequest().authenticated() // الباقي خاصو auth
            )
            .formLogin(form -> form.disable()) // نوقف form login
            .httpBasic(basic -> basic.disable()); // نوقف basic login

        return http.build();
    }
}

// package com.blog.blogger.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.web.SecurityFilterChain;

// @Configuration
// @EnableWebSecurity
// public class SecurityConfig {

//     @Bean
//     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//         http
//             // نطفي CSRF نهائياً
//             .csrf(csrf -> csrf.disable())
//             // نسمح بالروتات ديال auth
//             .authorizeHttpRequests(auth -> auth
//                 .requestMatchers("/auth/**", "/css/**", "/js/**").permitAll()
//                 .anyRequest().authenticated()
//             )
//             // نحيد أي نوع ديال form login أو basic login
//             .formLogin(form -> form.disable())
//             .httpBasic(basic -> basic.disable());

//         return http.build();
//     }
// }

