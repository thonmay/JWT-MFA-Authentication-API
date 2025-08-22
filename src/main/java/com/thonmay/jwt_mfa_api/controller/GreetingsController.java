package com.thonmay.jwt_mfa_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/greetings")
public class GreetingsController {

    @GetMapping
    public ResponseEntity<String> sayHello(Principal principal) {
        // Automatically populated by Spring Security from the SecurityContext
        return ResponseEntity.ok("Hello, " + principal.getName() + "! This is a protected endpoint.");
    }
}
