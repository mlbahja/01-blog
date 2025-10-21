package com.blog.blogger.Controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blog.blogger.Models.Post;
import com.blog.blogger.Services.PostService;

@RestController
@RequestMapping("/api")
public class Controllers {
    private final PostService postService = new PostService();

    @GetMapping("/home")
    public String home() {
        return "hello home should affiche posts of users";
    }

    @GetMapping("/login")
    public String login() {
        return "test one";
    }

    @GetMapping("/register")
    public String register() {
        return "register of user in blog for the first time";
    }

    @GetMapping("/posts")
    public List<Post> posts() {
        return postService.getAllPosts();
    }
}
