package com.blog.blogger;

import org.springframework.web.bind.annotation.GetMapping;
import  org.springframework.web.bind.annotation.RestController;

@RestController("/")
public class Controllers {
        @GetMapping("/home")
        public String home() {
            return "hello home";
        }
}