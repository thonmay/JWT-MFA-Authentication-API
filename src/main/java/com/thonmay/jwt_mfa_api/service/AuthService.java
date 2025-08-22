package com.thonmay.jwt_mfa_api.service;

import com.thonmay.jwt_mfa_api.dto.RegisterRequest;
import com.thonmay.jwt_mfa_api.model.User;
import com.thonmay.jwt_mfa_api.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.thonmay.jwt_mfa_api.model.Role;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(RegisterRequest registerRequest) {
        if (userRepository.findByUsername(registerRequest.username()).isPresent()) {
            throw new IllegalStateException("Username already exists");
        }

        User user = new User();
        user.setUsername(registerRequest.username());
        user.setPassword(passwordEncoder.encode(registerRequest.password()));


        user.setRole(Role.USER);
        user.setMfaEnabled(false); // MFA is disabled by default upon registration

        userRepository.save(user);
    }
}