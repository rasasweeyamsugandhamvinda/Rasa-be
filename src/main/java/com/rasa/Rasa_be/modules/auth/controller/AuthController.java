package com.rasa.Rasa_be.modules.auth.controller;

import com.rasa.Rasa_be.modules.auth.dto.*;
import com.rasa.Rasa_be.modules.auth.service.AuthService;
import com.rasa.Rasa_be.modules.shared.dto.ApiResponse;
import com.rasa.Rasa_be.modules.shared.dto.MessageResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

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

    @GetMapping("/sessions/{userId}")
    public ResponseEntity<ApiResponse<List<UserSessionResponse>>> getActiveSessions(@PathVariable UUID userId) {
        log.info("Retrieving active sessions for user: {}", userId);
        List<UserSessionResponse> sessions = authService.getActiveSessions(userId);

        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK, sessions, "Active sessions retrieved successfully.")
        );
    }

    @DeleteMapping("/sessions/{userId}")
    public ResponseEntity<ApiResponse<MessageResponse>> revokeAllSessions(@PathVariable UUID userId) {
        log.info("Revoking all active sessions for user: {}", userId);
        MessageResponse response = authService.revokeAllSessions(userId);

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