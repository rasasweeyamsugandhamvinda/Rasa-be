package com.rasa.Rasa_be.modules.auth.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.rasa.Rasa_be.modules.auth.domain.AuthProvider;
import com.rasa.Rasa_be.modules.auth.dto.OAuthLoginRequest;
import com.rasa.Rasa_be.modules.auth.dto.TokenResponse;
import com.rasa.Rasa_be.modules.auth.entity.OAuthIdentity;
import com.rasa.Rasa_be.modules.auth.entity.User;
import com.rasa.Rasa_be.modules.auth.entity.UserSession;
import com.rasa.Rasa_be.modules.auth.repository.UserRepository;
import com.rasa.Rasa_be.modules.auth.repository.UserSessionRepository;
import com.rasa.Rasa_be.modules.auth.service.JwtTokenService;
import com.rasa.Rasa_be.modules.auth.service.OAuthService;
import com.rasa.Rasa_be.modules.shared.exception.AppErrorCode;
import com.rasa.Rasa_be.modules.shared.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;

@Slf4j
@Service
public class OAuthServiceImpl implements OAuthService {

    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final JwtTokenService jwtTokenService;
    private final GoogleIdTokenVerifier googleVerifier;

    public OAuthServiceImpl(
            UserRepository userRepository,
            UserSessionRepository userSessionRepository,
            JwtTokenService jwtTokenService,
            @Value("${rasa.security.oauth.google.client-id}") String googleClientId
    ) {
        this.userRepository = userRepository;
        this.userSessionRepository = userSessionRepository;
        this.jwtTokenService = jwtTokenService;

        this.googleVerifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();
    }

    @Override
    public TokenResponse processGoogleLogin(OAuthLoginRequest request, String deviceInfo, String ipAddress) {
        try {

            GoogleIdToken idToken = googleVerifier.verify(request.idToken());
            if (idToken == null) {
                throw new AppException(AppErrorCode.TOKEN_INVALID, "Invalid Google ID Token");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String subjectId = payload.getSubject(); // Google's unique ID for this user

            log.info("Successfully verified Google ID token for email: {}", email);

            User user = userRepository.findByEmail(email).orElse(null);

            if (user == null) {
                user = User.builder()
                        .email(email)
                        .isVerified(true)
                        .authProvider(AuthProvider.GOOGLE)
                        .build();

                OAuthIdentity identity = OAuthIdentity.builder()
                        .provider(AuthProvider.GOOGLE)
                        .providerSubjectId(subjectId)
                        .build();

                user.addOAuthIdentity(identity);
                user = userRepository.save(user);
                log.info("Created new user via Google OAuth: {}", user.getId());

            } else {

                boolean hasGoogleIdentity = user.getOauthIdentities().stream()
                        .anyMatch(id -> id.getProvider() == AuthProvider.GOOGLE);

                if (!hasGoogleIdentity) {
                    OAuthIdentity identity = OAuthIdentity.builder()
                            .provider(AuthProvider.GOOGLE)
                            .providerSubjectId(subjectId)
                            .build();
                    user.addOAuthIdentity(identity);
                    user.setVerified(true); // If they weren't verified before, they are now.
                    userRepository.save(user);
                    log.info("Linked Google identity to existing user: {}", user.getId());
                }
            }

            return createSessionAndGenerateTokens(user, deviceInfo, ipAddress);

        } catch (Exception e) {
            log.error("Google OAuth verification failed", e);
            throw new AppException(AppErrorCode.UNAUTHORIZED, "OAuth authentication failed");
        }
    }

    private TokenResponse createSessionAndGenerateTokens(User user, String deviceInfo, String ipAddress) {
        String rawRefreshToken = jwtTokenService.generateRefreshToken();
        String refreshTokenHash = jwtTokenService.hashToken(rawRefreshToken);

        UserSession session = UserSession.builder()
                .user(user)
                .refreshTokenHash(refreshTokenHash)
                .deviceInfo(deviceInfo)
                .ipAddress(ipAddress)
                .isRevoked(false)
                .expiresAt(LocalDateTime.now().plusDays(30))
                .build();

        userSessionRepository.save(session);

        String accessToken = jwtTokenService.generateAccessToken(user.getId(), user.getEmail());

        return TokenResponse.bearer(accessToken, rawRefreshToken, jwtTokenService.getExpirationMs());
    }
}
