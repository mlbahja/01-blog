package com.blog.blogger.controller;
import java.util.Optional;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blog.blogger.models.User;
import com.blog.blogger.service.UserService;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")

public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return userService.register(user);
    }

    @PostMapping("/login")
    public Optional<User> login(@RequestBody User user) {
        return userService.login(user.getEmail(), user.getPassword());
    }
    // public static class UserDTO {
    //     public String username;
    //     public String password;
    // }

    // @PostMapping("/register")
    // public String register(@RequestBody UserDTO user) {
    //     System.out.println("User registered: " + user.username);
    //     return "User " + user.username + " registered successfully!";
    // }

    // @PostMapping("/login")
    // public String login(@RequestBody UserDTO user) {
      
    //     System.out.println("User login attempt: " + user.username);
    //     return "Welcome back " + user.username + "!";
    // }

    // @GetMapping("/posts")
    // public List<Post> posts() {
    //     return postService.getAllPosts();
    // }

    // public String getMethodName(@RequestParam String param) {
    //     return new String();
    // }
    
}
