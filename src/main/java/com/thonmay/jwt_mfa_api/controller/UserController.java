package com.thonmay.jwt_mfa_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/data")
    public ResponseEntity<String> getUserData() {
        return ResponseEntity.ok("This is regular USER data.");
    }
}