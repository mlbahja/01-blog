package com.blog.blogger.Controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import  org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class Controllers {
        @GetMapping("/home")
        public String home() {
            return "hello home should affiche posts of users";
        }
        @GetMapping("/login")
        public String login(/*@RequestParam String param*/) {
            // return new String();
            return "test one";
        }
        @GetMapping("/register")
        public String register(){
            return "register of user in blog for the first time";
        }
        
}