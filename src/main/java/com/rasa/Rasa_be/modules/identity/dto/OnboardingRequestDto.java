package com.rasa.Rasa_be.modules.identity.dto;

import com.rasa.Rasa_be.modules.identity.entity.enums.ExperienceLevel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OnboardingRequestDto {

    @NotNull(message = "Experience level is required")
    private ExperienceLevel experienceLevel;

    @Valid
    private LifestyleDto lifestyle;

    @Valid
    private PreferencesDto preferences;

    private List<@Valid PastInteractionDto> pastInteractions;
}
