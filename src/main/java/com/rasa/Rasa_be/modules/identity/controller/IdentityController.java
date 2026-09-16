package com.rasa.Rasa_be.modules.identity.controller;

import com.rasa.Rasa_be.config.security.UserPrincipal;
import com.rasa.Rasa_be.modules.identity.dto.*;
import com.rasa.Rasa_be.modules.identity.service.IdentityService;
import com.rasa.Rasa_be.modules.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/identity")
@RequiredArgsConstructor
@Slf4j
public class IdentityController {

    private final IdentityService identityService;

    @PostMapping("/onboarding")
    public ResponseEntity<ApiResponse<Void>> onboarding(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody OnboardingRequestDto request) {

        log.info("Onboarding request received for user: {}", principal.id());
        identityService.processOnboarding(principal.id(), request);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, null, "Onboarding completed successfully."));
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<UserStatusResponseDto>> getOnboardingStatus(
            @AuthenticationPrincipal UserPrincipal principal) {

        log.info("Onboarding status check requested for user: {}", principal.id());
        UserStatusResponseDto status = identityService.getOnboardingStatus(principal.id());

        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK, status, "User onboarding status retrieved successfully.")
        );
    }
    @GetMapping("/profile")
    public ResponseEntity<IdentityProfileDto> getProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(identityService.getUserIdentityProfile(userPrincipal.id()));
    }

    @PatchMapping("/profile/lifestyle")
    public ResponseEntity<IdentityProfileDto> updateLifestyle(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody LifestyleDto request) {
        return ResponseEntity.ok(identityService.updateLifestyle(userPrincipal.id(), request));
    }

    @PatchMapping("/profile/preferences")
    public ResponseEntity<IdentityProfileDto> updatePreferences(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody PreferencesDto request) {
        return ResponseEntity.ok(identityService.updatePreferences(userPrincipal.id(), request));
    }
}
