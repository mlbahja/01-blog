package com.blog.blogger.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
public class AuthController {

    
    public static class UserDTO {
        public String username;
        public String password;
    }

    @PostMapping("/register")
    public String register(@RequestBody UserDTO user) {
        System.out.println("User registered: " + user.username);
        return "User " + user.username + " registered successfully!";
    }

    @PostMapping("/login")
    public String login(@RequestBody UserDTO user) {
      
        System.out.println("User login attempt: " + user.username);
        return "Welcome back " + user.username + "!";
    }
    // @GetMapping("/posts")
    // public List<Post> posts() {
    //     return postService.getAllPosts();
    // }

    // public String getMethodName(@RequestParam String param) {
    //     return new String();
    // }
    
}
