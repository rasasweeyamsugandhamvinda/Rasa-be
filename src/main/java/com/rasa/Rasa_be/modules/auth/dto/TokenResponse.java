package com.rasa.Rasa_be.modules.auth.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresInMs
) {
    public static TokenResponse bearer(String accessToken, String refreshToken, long expiresInMs) {
        return new TokenResponse(accessToken, refreshToken, "Bearer", expiresInMs);
    }
}