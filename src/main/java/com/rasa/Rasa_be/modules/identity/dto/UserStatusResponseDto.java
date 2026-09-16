package com.rasa.Rasa_be.modules.identity.dto;

import com.rasa.Rasa_be.modules.identity.entity.enums.ExperienceLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserStatusResponseDto {
    private UUID userId;
    private Boolean onboardingCompleted;
    private ExperienceLevel experienceLevel;
}