package com.rasa.Rasa_be.modules.identity.service.impl;

import com.rasa.Rasa_be.modules.identity.dto.*;
import com.rasa.Rasa_be.modules.identity.entity.FragrancePreferences;
import com.rasa.Rasa_be.modules.identity.entity.Interaction;
import com.rasa.Rasa_be.modules.identity.entity.LifestyleProfile;
import com.rasa.Rasa_be.modules.identity.entity.UserProfile;
import com.rasa.Rasa_be.modules.identity.entity.enums.InteractionSource;
import com.rasa.Rasa_be.modules.identity.repository.FragrancePreferencesRepository;
import com.rasa.Rasa_be.modules.identity.repository.InteractionRepository;
import com.rasa.Rasa_be.modules.identity.repository.LifestyleProfileRepository;
import com.rasa.Rasa_be.modules.identity.repository.UserProfileRepository;
import com.rasa.Rasa_be.modules.identity.service.IdentityService;
import com.rasa.Rasa_be.modules.shared.exception.AppErrorCode;
import com.rasa.Rasa_be.modules.shared.exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class IdentityServiceImpl implements IdentityService {

    private final UserProfileRepository userProfileRepository;
    private final LifestyleProfileRepository lifestyleProfileRepository;
    private final FragrancePreferencesRepository fragrancePreferencesRepository;
    private final InteractionRepository interactionRepository;

    @Override
    @Transactional
    public void processOnboarding(UUID userId, OnboardingRequestDto request) {
        log.info("Processing onboarding for user: {}", userId);

        UserProfile profile = getOrCreateUserProfile(userId);

        if (Boolean.TRUE.equals(profile.getOnboardingCompleted())) {
            throw new AppException(AppErrorCode.BUSINESS_RULE_VIOLATION, "User has already completed onboarding");
        }

        profile.setExperienceLevel(request.getExperienceLevel());
        profile.setOnboardingCompleted(true);
        userProfileRepository.save(profile);

        Optional.ofNullable(request.getLifestyle()).ifPresent(dto -> saveLifestyleProfile(userId, dto));
        Optional.ofNullable(request.getPreferences()).ifPresent(dto -> saveFragrancePreferences(userId, dto));
        Optional.ofNullable(request.getPastInteractions()).ifPresent(dtos -> savePastInteractions(userId, profile, dtos));

        log.info("Onboarding completed successfully for user: {}", userId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserStatusResponseDto getOnboardingStatus(UUID userId) {
        return userProfileRepository.findById(userId)
                .map(profile -> UserStatusResponseDto.builder()
                        .userId(profile.getUserId())
                        .onboardingCompleted(profile.getOnboardingCompleted())
                        .experienceLevel(profile.getExperienceLevel())
                        .build())
                .orElseGet(() -> UserStatusResponseDto.builder()
                        .userId(userId)
                        .onboardingCompleted(false)
                        .build());
    }

    @Override
    @Transactional(readOnly = true)
    public IdentityProfileDto getUserIdentityProfile(UUID userId) {
        UserProfile profile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new AppException(AppErrorCode.RESOURCE_NOT_FOUND, "User profile not found"));

        LifestyleProfile lifestyle = lifestyleProfileRepository.findById(userId).orElseGet(LifestyleProfile::new);
        FragrancePreferences prefs = fragrancePreferencesRepository.findById(userId).orElseGet(FragrancePreferences::new);

        return IdentityProfileDto.builder()
                .userId(userId)
                .experienceLevel(profile.getExperienceLevel())
                .city(lifestyle.getCity())
                .state(lifestyle.getState())
                .primaryEnvironment(lifestyle.getPrimaryEnvironment())
                .sweatLevel(lifestyle.getSweatLevel())
                .vibePreference(lifestyle.getVibePreference())
                .budgetPreference(lifestyle.getBudgetPreference())
                .likedNoteIds(toList(prefs.getLikedNoteIds()))
                .dislikedNoteIds(toList(prefs.getDislikedNoteIds()))
                .preferredAccordIds(toList(prefs.getPreferredAccordIds()))
                .dislikedAccordIds(toList(prefs.getDislikedAccordIds()))
                .build();
    }

    @Override
    @Transactional
    public IdentityProfileDto updateLifestyle(UUID userId, LifestyleDto request) {
        LifestyleProfile lifestyle = lifestyleProfileRepository.findById(userId)
                .orElseThrow(() -> new AppException(AppErrorCode.RESOURCE_NOT_FOUND, "Lifestyle profile not found"));

        applyLifestyleUpdates(lifestyle, request);
        lifestyleProfileRepository.save(lifestyle);

        return getUserIdentityProfile(userId);
    }

    @Override
    @Transactional
    public IdentityProfileDto updatePreferences(UUID userId, PreferencesDto request) {
        FragrancePreferences preferences = fragrancePreferencesRepository.findById(userId)
                .orElseThrow(() -> new AppException(AppErrorCode.RESOURCE_NOT_FOUND, "Preferences not found"));

        applyPreferenceUpdates(preferences, request);
        fragrancePreferencesRepository.save(preferences);

        return getUserIdentityProfile(userId);
    }

    // --- Private Extract/Helper Methods ---

    private UserProfile getOrCreateUserProfile(UUID userId) {
        return userProfileRepository.findById(userId).orElseGet(() -> {
            log.info("Creating new UserProfile for user: {}", userId);
            UserProfile newProfile = new UserProfile();
            newProfile.setUserId(userId);
            newProfile.setOnboardingCompleted(false);
            return newProfile;
        });
    }

    private void saveLifestyleProfile(UUID userId, LifestyleDto dto) {
        LifestyleProfile lifestyle = lifestyleProfileRepository.findById(userId).orElseGet(() -> {
            LifestyleProfile newLifestyle = new LifestyleProfile();
            newLifestyle.setUserId(userId);
            return newLifestyle;
        });
        applyLifestyleUpdates(lifestyle, dto);
        lifestyleProfileRepository.save(lifestyle);
    }

    private void saveFragrancePreferences(UUID userId, PreferencesDto dto) {
        FragrancePreferences prefs = fragrancePreferencesRepository.findById(userId).orElseGet(() -> {
            FragrancePreferences newPrefs = new FragrancePreferences();
            newPrefs.setUserId(userId);
            return newPrefs;
        });
        applyPreferenceUpdates(prefs, dto);
        fragrancePreferencesRepository.save(prefs);
    }

    private void applyLifestyleUpdates(LifestyleProfile lifestyle, LifestyleDto dto) {
        if (dto.getCity() != null) lifestyle.setCity(dto.getCity());
        if (dto.getState() != null) lifestyle.setState(dto.getState());
        if (dto.getPrimaryEnvironment() != null) lifestyle.setPrimaryEnvironment(dto.getPrimaryEnvironment());
        if (dto.getSweatLevel() != null) lifestyle.setSweatLevel(dto.getSweatLevel());
        if (dto.getVibePreference() != null) lifestyle.setVibePreference(dto.getVibePreference());
        if (dto.getBudgetPreference() != null) lifestyle.setBudgetPreference(dto.getBudgetPreference());
    }

    private void applyPreferenceUpdates(FragrancePreferences prefs, PreferencesDto dto) {
        if (dto.getLikedNoteIds() != null) prefs.setLikedNoteIds(toArray(dto.getLikedNoteIds()));
        if (dto.getDislikedNoteIds() != null) prefs.setDislikedNoteIds(toArray(dto.getDislikedNoteIds()));
        if (dto.getPreferredAccordIds() != null) prefs.setPreferredAccordIds(toArray(dto.getPreferredAccordIds()));
        if (dto.getDislikedAccordIds() != null) prefs.setDislikedAccordIds(toArray(dto.getDislikedAccordIds()));
    }

    private void savePastInteractions(UUID userId, UserProfile profile, List<PastInteractionDto> dtos) {
        Set<UUID> existingPerfumeIds = interactionRepository
                .findPerfumeIdsByUserIdAndSource(userId, InteractionSource.ONBOARDING);

        List<Interaction> newInteractions = dtos.stream()
                .filter(dto -> !existingPerfumeIds.contains(dto.getPerfumeId()))
                .map(dto -> {
                    Interaction interaction = new Interaction();
                    interaction.setUserId(userId);
                    interaction.setUserProfile(profile);
                    interaction.setPerfumeId(dto.getPerfumeId());
                    interaction.setSource(InteractionSource.ONBOARDING);
                    interaction.setSignalType(dto.getSignalType());
                    return interaction;
                })
                .collect(Collectors.toList());

        if (!newInteractions.isEmpty()) {
            interactionRepository.saveAll(newInteractions);
            log.debug("Saved {} new onboarding interactions for user: {}", newInteractions.size(), userId);
        }
    }

    // Array / List conversion helpers to keep mapping logic safe and clean
    private UUID[] toArray(List<UUID> list) {
        return (list == null || list.isEmpty()) ? null : list.toArray(new UUID[0]);
    }

    private List<UUID> toList(UUID[] array) {
        return (array == null || array.length == 0) ? List.of() : List.of(array);
    }
}