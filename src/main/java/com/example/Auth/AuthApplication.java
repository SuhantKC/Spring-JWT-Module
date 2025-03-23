package com.example.Auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }

    @GetMapping("/auth/test")
    public String successMapping() {
        return "If you see this, it means the mapping is working";
    }
    @GetMapping("/test")
    public String failureMapping() {
        return "if you see this, it means the mapping is not working";
    }



}