package com.thonmay.jwt_mfa_api.controller;

import com.thonmay.jwt_mfa_api.dto.LoginRequest;
import com.thonmay.jwt_mfa_api.dto.LoginResponse;
import com.thonmay.jwt_mfa_api.dto.RegisterRequest;
import com.thonmay.jwt_mfa_api.model.User;
import com.thonmay.jwt_mfa_api.repository.UserRepository;
import com.thonmay.jwt_mfa_api.service.AuthService;
import com.thonmay.jwt_mfa_api.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.thonmay.jwt_mfa_api.dto.MfaVerificationRequest;
import com.thonmay.jwt_mfa_api.service.TotpService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, AuthenticationManager authenticationManager,
                          UserDetailsService userDetailsService, JwtService jwtService,
                          UserRepository userRepository) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        try {
            authService.register(registerRequest);
            return ResponseEntity.ok("User registered successfully!");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }

        User user = userRepository.findByUsername(loginRequest.username()).orElseThrow();

        if (user.isMfaEnabled()) {
            // MFA is enabled, return response indicating MFA is required
            return ResponseEntity.ok(new LoginResponse(null, true));
        }

        // MFA is not enabled, generate JWT and return
        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.username());
        final String jwt = jwtService.generateToken(userDetails);
        return ResponseEntity.ok(new LoginResponse(jwt, false));
    }

    @PostMapping("/verify-mfa")
    public ResponseEntity<?> verifyMfa(@RequestBody MfaVerificationRequest verificationRequest) {
        User user = userRepository.findByUsername(verificationRequest.username())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!TotpService.isCodeValid(user.getMfaSecret(), verificationRequest.code())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid MFA code");
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(verificationRequest.username());
        final String jwt = jwtService.generateToken(userDetails);
        return ResponseEntity.ok(new LoginResponse(jwt, false));
    }

}



