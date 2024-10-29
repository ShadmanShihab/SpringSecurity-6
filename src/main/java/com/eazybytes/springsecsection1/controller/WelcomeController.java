package com.eazybytes.springsecsection1.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {
    @GetMapping("/contact")
    public ResponseEntity<String> welcome() {
        return ResponseEntity.ok("Welcome to Spring Boot!");
    }
}
