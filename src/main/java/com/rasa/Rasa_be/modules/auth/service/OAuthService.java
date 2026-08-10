package com.rasa.Rasa_be.modules.auth.service;

import com.rasa.Rasa_be.modules.auth.dto.OAuthLoginRequest;
import com.rasa.Rasa_be.modules.auth.dto.TokenResponse;

public interface OAuthService {
    TokenResponse processGoogleLogin(OAuthLoginRequest request, String deviceInfo, String ipAddress);
}
