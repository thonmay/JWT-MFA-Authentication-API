package com.thonmay.jwt_mfa_api.dto;

public record MfaSetupResponse(String secret, String qrCodeUri) {}
