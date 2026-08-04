package com.rasa.Rasa_be.modules.auth.service;

import com.rasa.Rasa_be.modules.auth.dto.*;
import com.rasa.Rasa_be.modules.shared.dto.MessageResponse;

import java.util.List;
import java.util.UUID;

public interface AuthService {
    public MessageResponse initiateAuth(InitiateAuthRequest request);
    public TokenResponse verifyOtpAndRegister(VerifyOtpRequest request, String deviceInfo, String ipAddress);
    public TokenResponse login(LoginRequest request, String deviceInfo, String ipAddress);
    public TokenResponse refreshToken(RefreshTokenRequest request);
    public MessageResponse logout(String rawRefreshToken);
    public MessageResponse revokeAllSessions(UUID userId);
    public List<UserSessionResponse> getActiveSessions(UUID userId);
}
