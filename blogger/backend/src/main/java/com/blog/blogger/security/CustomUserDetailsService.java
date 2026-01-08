package com.blog.blogger.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.blog.blogger.models.User;
import com.blog.blogger.repository.UserRepository;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 
     *
     *
     *
     * @param username - Can be username or email
     * @return UserDetails - Spring Security's user format
     * @throws UsernameNotFoundException if user not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       
        User user = userRepository.findByEmail(username)
                .orElseGet(() ->
                   
                    userRepository.findByUsername(username)
                            .orElseThrow(() ->
                                new UsernameNotFoundException("User not found: " + username)
                            )
                );

      
        if (user.getIsBanned() != null && user.getIsBanned()) {
            throw new UsernameNotFoundException("User account is banned: " + username);
        }

        
        if (user.getUsername() == null || user.getPassword() == null || user.getRole() == null) {
            throw new UsernameNotFoundException("User data is incomplete for: " + username);
        }

        
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())              
                .password(user.getPassword())              
                .roles(user.getRole().name())              
                .build();
    }

    /**
     * 
     * 
     *
     * @param username 
     * @return 
     * @throws UsernameNotFoundException 
     */
    public User getUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseGet(() ->
                    userRepository.findByUsername(username)
                            .orElseThrow(() ->
                                new UsernameNotFoundException("User not found: " + username)
                            )
                );

        if (user.getIsBanned() != null && user.getIsBanned()) {
            throw new UsernameNotFoundException("User account is banned: " + username);
        }

        return user;
    }
}
