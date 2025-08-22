package com.thonmay.jwt_mfa_api.controller;

import com.thonmay.jwt_mfa_api.dto.MfaSetupResponse;
import com.thonmay.jwt_mfa_api.dto.MfaVerificationRequest;
import com.thonmay.jwt_mfa_api.model.User;
import com.thonmay.jwt_mfa_api.repository.UserRepository;
import com.thonmay.jwt_mfa_api.service.TotpService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/mfa")
public class MfaController {

    private final TotpService totpService;
    private final UserRepository userRepository;

    public MfaController(TotpService totpService, UserRepository userRepository) {
        this.totpService = totpService;
        this.userRepository = userRepository;
    }

    @PostMapping("/setup")
    public ResponseEntity<MfaSetupResponse> setupMfa(Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isMfaEnabled()) {
            return ResponseEntity.badRequest().build(); // Already enabled
        }

        final String secret = totpService.generateNewSecret();
        final String qrCodeUri = totpService.generateQrCodeImageUri(secret, user.getUsername());

        // Temporarily store secret in DB before verification
        user.setMfaSecret(secret);
        userRepository.save(user);

        return ResponseEntity.ok(new MfaSetupResponse(secret, qrCodeUri));
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyMfaSetup(@RequestBody MfaVerificationRequest verificationRequest, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!totpService.isCodeValid(user.getMfaSecret(), verificationRequest.code())) {
            user.setMfaSecret(null);
            userRepository.save(user);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid MFA code");
        }

        user.setMfaEnabled(true);
        userRepository.save(user);

        return ResponseEntity.ok("MFA has been enabled successfully.");
    }
}
