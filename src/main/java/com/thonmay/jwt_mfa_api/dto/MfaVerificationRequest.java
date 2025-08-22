package com.thonmay.jwt_mfa_api.dto;

// Used for both initial verification and subsequent logins
public record MfaVerificationRequest(String username, String code) {}