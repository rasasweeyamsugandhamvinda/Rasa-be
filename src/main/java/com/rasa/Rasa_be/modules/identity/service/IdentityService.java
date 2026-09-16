package com.rasa.Rasa_be.modules.identity.service;

import com.rasa.Rasa_be.modules.identity.dto.*;

import java.util.UUID;

public interface IdentityService {

    void processOnboarding(UUID userId, OnboardingRequestDto request);
    UserStatusResponseDto getOnboardingStatus(UUID userId);
    IdentityProfileDto getUserIdentityProfile(UUID userId);
    IdentityProfileDto updateLifestyle(UUID userId, LifestyleDto request);
    IdentityProfileDto updatePreferences(UUID userId, PreferencesDto request);

}
