package com.rasa.Rasa_be.modules.recommendation.dto;

import com.rasa.Rasa_be.modules.identity.entity.enums.BudgetPreference;
import com.rasa.Rasa_be.modules.identity.entity.enums.PrimaryEnvironment;
import com.rasa.Rasa_be.modules.identity.entity.enums.SweatLevel;
import com.rasa.Rasa_be.modules.identity.entity.enums.VibePreference;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RecommendationRequestDto {
    private String prompt;
    private String occasion;
    private String mood;
    private BudgetPreference budgetOverride; // New field for session-level overrides
    private PrimaryEnvironment environmentOverride;
    private SweatLevel sweatLevelOverride;
    private VibePreference vibePreferenceOverride;
    private List<String> requestedAccords = new ArrayList<>();
}