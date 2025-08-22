package com.thonmay.jwt_mfa_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/data")
    public ResponseEntity<String> getAdminData() {
        return ResponseEntity.ok("This is sensitive ADMIN data!");
    }
}
