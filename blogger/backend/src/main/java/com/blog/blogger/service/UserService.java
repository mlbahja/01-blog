/*
package com.blog.blogger.service;
import java.util.Optional;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.blog.blogger.models.User;
import com.blog.blogger.models.Role;
import com.blog.blogger.repository.UserRepository;



@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(User user) {
         System.out.println("------------------------");
         System.out.println();
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        return userRepository.save(user);
    }

    public Optional<User> login(String email, String password) {
      

        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent() && passwordEncoder.matches(password, existingUser.get().getPassword())) {
            return existingUser;    
        }
        System.out.println("test login .. ");
        return Optional.empty();
    }
}
*/

package com.blog.blogger.service;

import java.util.Optional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.blog.blogger.models.User;
import com.blog.blogger.models.Role;
import com.blog.blogger.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

 
    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        return userRepository.save(user);
    }

    
    public Optional<User> login(String email, String password) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent() && passwordEncoder.matches(password, existingUser.get().getPassword())) {
            return existingUser;
        }
        return Optional.empty();
    }


    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}
