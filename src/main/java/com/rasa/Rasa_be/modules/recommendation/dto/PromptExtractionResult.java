package com.rasa.Rasa_be.modules.recommendation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rasa.Rasa_be.modules.identity.entity.enums.BudgetPreference;
import com.rasa.Rasa_be.modules.identity.entity.enums.PrimaryEnvironment;
import com.rasa.Rasa_be.modules.identity.entity.enums.SweatLevel;
import com.rasa.Rasa_be.modules.identity.entity.enums.VibePreference;

import java.util.List;

public record PromptExtractionResult(
        Occasion occasion,
        Mood mood,
        PrimaryEnvironment environmentOverride,
        SweatLevel sweatLevelOverride,
        BudgetPreference budgetOverride,
        VibePreference vibePreferenceOverride,
        List<String> requestedAccords
) {
    public enum Occasion {
        OFFICE_DAYTIME,
        ROMANTIC_EVENING
    }

    public enum Mood {
        CONFIDENT,
        RELAXED
    }
}