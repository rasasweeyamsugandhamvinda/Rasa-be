package com.rasa.Rasa_be.modules.auth.controller;

import com.rasa.Rasa_be.config.security.UserPrincipal;
import com.rasa.Rasa_be.modules.auth.dto.*;
import com.rasa.Rasa_be.modules.auth.service.AuthService;
import com.rasa.Rasa_be.modules.auth.service.OAuthService;
import com.rasa.Rasa_be.modules.shared.dto.ApiResponse;
import com.rasa.Rasa_be.modules.shared.dto.MessageResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final OAuthService oAuthService;

    @PostMapping("/initiate")
    public ResponseEntity<ApiResponse<MessageResponse>> initiateAuth(@Valid @RequestBody InitiateAuthRequest request) {
        log.info("Received request to initiate auth for email: {}", request.email());
        MessageResponse response = authService.initiateAuth(request);

        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK, response, response.message())
        );
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<TokenResponse>> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request,
            HttpServletRequest httpRequest) {

        String deviceInfo = httpRequest.getHeader("User-Agent");
        String ipAddress = getClientIp(httpRequest);
        log.info("Received OTP verification request for email: {} from IP: {}", request.email(), ipAddress);

        TokenResponse tokens = authService.verifyOtpAndRegister(request, deviceInfo, ipAddress);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(HttpStatus.CREATED, tokens, "Account activated and authenticated successfully.")
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {

        String deviceInfo = httpRequest.getHeader("User-Agent");
        String ipAddress = getClientIp(httpRequest);
        log.info("Received login request for email: {} from IP: {}", request.email(), ipAddress);

        TokenResponse tokens = authService.login(request, deviceInfo, ipAddress);

        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK, tokens, "Login successful.")
        );
    }

    @PostMapping("/oauth/google")
    public ResponseEntity<ApiResponse<TokenResponse>> loginWithGoogle(
            @Valid @RequestBody OAuthLoginRequest request,
            HttpServletRequest httpRequest) {

        String deviceInfo = httpRequest.getHeader("User-Agent");
        String ipAddress = getClientIp(httpRequest);
        log.info("Received Google OAuth login request from IP: {}", ipAddress);

        TokenResponse tokens = oAuthService.processGoogleLogin(request, deviceInfo, ipAddress);

        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK, tokens, "Google authentication successful.")
        );
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(@AuthenticationPrincipal UserPrincipal principal) {
        log.info("Fetching profile for authenticated user: {}", principal.id());
        UserResponse response = authService.getCurrentUser(principal.id());
        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK, response, "User profile retrieved")
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("Received token refresh request.");
        TokenResponse tokens = authService.refreshToken(request);

        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK, tokens, "Tokens refreshed successfully.")
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<MessageResponse>> logout(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("Received logout request.");
        MessageResponse response = authService.logout(request.refreshToken());

        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK, response, response.message())
        );
    }

    @GetMapping("/sessions")
    public ResponseEntity<ApiResponse<List<UserSessionResponse>>> getActiveSessions(@AuthenticationPrincipal UserPrincipal principal) {
        log.info("Retrieving active sessions for user: {}", principal.id());
        List<UserSessionResponse> sessions = authService.getActiveSessions(principal.id());

        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK, sessions, "Active sessions retrieved successfully.")
        );
    }

    @DeleteMapping("/sessions")
    public ResponseEntity<ApiResponse<MessageResponse>> revokeAllSessions(@AuthenticationPrincipal UserPrincipal principal) {
        log.info("Revoking all active sessions for user: {}", principal.id());
        MessageResponse response = authService.revokeAllSessions(principal.id());

        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK, response, response.message())
        );
    }

    /**
     * Helper method to extract the real IP address, bypassing standard proxies or load balancers.
     */
    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty() || !xfHeader.contains(request.getRemoteAddr())) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }
}