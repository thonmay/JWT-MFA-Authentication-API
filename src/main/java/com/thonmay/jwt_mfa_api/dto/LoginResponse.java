package com.thonmay.jwt_mfa_api.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private boolean mfaRequired;

    public LoginResponse(String token, boolean mfaRequired) {
        this.token = token;
        this.mfaRequired = mfaRequired;
    }
}
