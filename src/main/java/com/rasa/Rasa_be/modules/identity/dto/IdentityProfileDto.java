package com.rasa.Rasa_be.modules.identity.dto;

import com.rasa.Rasa_be.modules.identity.entity.enums.*;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class IdentityProfileDto {
    private UUID userId;
    private ExperienceLevel experienceLevel;
    private String city;
    private String state;
    private PrimaryEnvironment primaryEnvironment;
    private SweatLevel sweatLevel;
    private VibePreference vibePreference;
    private BudgetPreference budgetPreference;
    private List<UUID> likedNoteIds;
    private List<UUID> dislikedNoteIds;
    private List<UUID> preferredAccordIds;
    private List<UUID> dislikedAccordIds;
}