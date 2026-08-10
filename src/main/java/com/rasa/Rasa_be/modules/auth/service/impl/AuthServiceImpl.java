package com.rasa.Rasa_be.modules.auth.service.impl;

import com.rasa.Rasa_be.modules.auth.service.AuthService;
import com.rasa.Rasa_be.modules.auth.domain.AuthProvider;
import com.rasa.Rasa_be.modules.auth.dto.*;
import com.rasa.Rasa_be.modules.auth.entity.User;
import com.rasa.Rasa_be.modules.auth.entity.UserSession;
import com.rasa.Rasa_be.modules.auth.repository.UserRepository;
import com.rasa.Rasa_be.modules.auth.repository.UserSessionRepository;
import com.rasa.Rasa_be.modules.auth.service.JwtTokenService;
import com.rasa.Rasa_be.modules.auth.service.RedisOtpService;
import com.rasa.Rasa_be.modules.shared.dto.MessageResponse;
import com.rasa.Rasa_be.modules.shared.exception.AppErrorCode;
import com.rasa.Rasa_be.modules.shared.exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final JwtTokenService jwtTokenService;
    private final RedisOtpService redisOtpService;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    @Transactional
    public MessageResponse initiateAuth(InitiateAuthRequest request) {
        String email = request.email().toLowerCase().trim();
        boolean userExists = userRepository.existsByEmail(email);

        if (userExists) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(AppErrorCode.RESOURCE_NOT_FOUND, "User not found"));

            if (user.isVerified()) {
                throw new AppException(AppErrorCode.BUSINESS_RULE_VIOLATION, "User with email " + email + " is already verified. Please Log in.");
            }
            log.info("Resending OTP for unverified existing user: {}", email);
        } else {
            User newUser = User.builder()
                    .email(email)
                    .isVerified(false)
                    .authProvider(AuthProvider.LOCAL)
                    .build();
            userRepository.save(newUser);
            log.info("Created new unverified user record for email: {}", email);
        }

        String otp = String.format("%06d", secureRandom.nextInt(1_000_000));
        log.info("New OTP: {}", otp);
        redisOtpService.storeOtp(email, otp);

        log.info("Generated OTP for email: {}. (Dispatching via RabbitMQ)", email);

        return new MessageResponse("OTP has been sent successfully to your email.");
    }

    @Override
    @Transactional
    public TokenResponse verifyOtpAndRegister(VerifyOtpRequest request, String deviceInfo, String ipAddress) {
        String email = request.email().toLowerCase().trim();

        if (!redisOtpService.verifyOtp(email, request.otp())) {
            throw new AppException(AppErrorCode.UNAUTHORIZED, "Invalid or expired OTP");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(AppErrorCode.RESOURCE_NOT_FOUND, "User not found"));

        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setVerified(true);
        userRepository.save(user);

        log.info("Successfully verified OTP and activated account for user: {}", user.getId());

        return createSessionAndGenerateTokens(user, deviceInfo, ipAddress);
    }

    @Override
    @Transactional
    public TokenResponse login(LoginRequest request, String deviceInfo, String ipAddress) {
        String email = request.email().toLowerCase().trim();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(AppErrorCode.INVALID_CREDENTIALS, "Invalid credentials"));

        if (!user.isVerified()) {
            throw new AppException(AppErrorCode.UNAUTHORIZED, "Account is not verified. Please complete OTP verification.");
        }

        if (user.getPasswordHash() == null || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            log.warn("Failed login attempt for user: {}", email);
            throw new AppException(AppErrorCode.INVALID_CREDENTIALS, "Invalid credentials");
        }

        log.info("User authenticated successfully: {}", user.getId());
        return createSessionAndGenerateTokens(user, deviceInfo, ipAddress);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(AppErrorCode.RESOURCE_NOT_FOUND, "User not found"));

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.isVerified(),
                user.getAuthProvider(),
                user.getCreatedAt()
        );
    }

    @Override
    @Transactional
    public TokenResponse refreshToken(RefreshTokenRequest request) {
        String rawRefreshToken = request.refreshToken();
        String refreshTokenHash = jwtTokenService.hashToken(rawRefreshToken);

        UserSession session = userSessionRepository.findByRefreshTokenHash(refreshTokenHash)
                .orElseThrow(() -> {
                    log.warn("Attempted refresh with unknown refresh token!");
                    return new AppException(AppErrorCode.TOKEN_INVALID, "Invalid refresh token");
                });

        if (session.isRevoked() || session.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.error("BREACH DETECTED: Revoked or expired refresh token used for User: {}. Revoking ALL user sessions!",
                    session.getUser().getId());
            userSessionRepository.revokeAllByUserId(session.getUser().getId());
            throw new AppException(AppErrorCode.SESSION_REVOKED, "Security compromise detected. All active sessions have been invalidated.");
        }

        String newRawRefreshToken = jwtTokenService.generateRefreshToken();
        String newRefreshTokenHash = jwtTokenService.hashToken(newRawRefreshToken);

        session.setRefreshTokenHash(newRefreshTokenHash);
        session.setExpiresAt(LocalDateTime.now().plusDays(30));
        userSessionRepository.save(session);

        String newAccessToken = jwtTokenService.generateAccessToken(
                session.getUser().getId(),
                session.getUser().getEmail()
        );

        log.info("Rotated refresh token for session: {} of user: {}", session.getId(), session.getUser().getId());

        return TokenResponse.bearer(newAccessToken, newRawRefreshToken, jwtTokenService.getExpirationMs());
    }

    @Override
    @Transactional
    public MessageResponse logout(String rawRefreshToken) {
        String refreshTokenHash = jwtTokenService.hashToken(rawRefreshToken);
        userSessionRepository.findByRefreshTokenHash(refreshTokenHash)
                .ifPresent(session -> {
                    session.revoke();
                    userSessionRepository.save(session);
                    log.info("Revoked session: {} for user: {}", session.getId(), session.getUser().getId());
                });
        return new MessageResponse("Successfully logged out.");
    }

    @Override
    @Transactional
    public MessageResponse revokeAllSessions(UUID userId) {
        userSessionRepository.revokeAllByUserId(userId);
        log.info("Revoked all active sessions for user: {}", userId);
        return new MessageResponse("All active sessions have been securely revoked.");
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSessionResponse> getActiveSessions(UUID userId) {
        return userSessionRepository.findAllByUserIdAndIsRevokedFalseAndExpiresAtAfter(userId, LocalDateTime.now())
                .stream()
                .map(s -> new UserSessionResponse(
                        s.getId(),
                        s.getDeviceInfo(),
                        s.getIpAddress(),
                        s.getCreatedAt(), // Inherited successfully from BaseAuditEntity
                        s.getExpiresAt()
                ))
                .toList();
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

//        user.addSession(session);
        userSessionRepository.save(session);

        String accessToken = jwtTokenService.generateAccessToken(user.getId(), user.getEmail());

        return TokenResponse.bearer(accessToken, rawRefreshToken, jwtTokenService.getExpirationMs());
    }
}