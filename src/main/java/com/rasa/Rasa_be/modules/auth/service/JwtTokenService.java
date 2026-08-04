package com.rasa.Rasa_be.modules.auth.service;

import io.jsonwebtoken.Claims;
import java.util.UUID;

public interface JwtTokenService {
    public String generateAccessToken(UUID userId, String email);
    public String generateRefreshToken();
    public String hashToken(String token);
    public Claims validateAndExtractClaims(String token);
    public long getExpirationMs();
}
