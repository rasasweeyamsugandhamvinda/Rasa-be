package com.rasa.Rasa_be.modules.recommendation.utility;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rasa.Rasa_be.modules.identity.entity.enums.ClimateZone;
import com.rasa.Rasa_be.modules.identity.entity.enums.PrimaryEnvironment;
import com.rasa.Rasa_be.modules.identity.entity.enums.SweatLevel;
import com.rasa.Rasa_be.modules.identity.entity.enums.VibePreference;
import com.rasa.Rasa_be.modules.recommendation.config.RecommendationRulesProperties;
import com.rasa.Rasa_be.modules.recommendation.dto.PromptExtractionResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class RecommendationRulesConsistencyValidator {

    private final RecommendationRulesProperties rulesProperties;

    @Value("${rasa.validation.strict-mode:true}")
    private boolean strictMode;

    private static final Set<String> ALLOWED_DIMENSIONS = Set.of(
            "occasion", "mood", "primaryEnvironment", "sweatLevel", "climate", "vibePreference"
    );

    @EventListener(ApplicationReadyEvent.class)
    public void validateRulesConsistency() {
        List<String> validationErrors = new ArrayList<>();

        verifyAlignment("Occasion", getJsonPropertyValues(PromptExtractionResult.Occasion.class),
                rulesProperties.getOccasion(), validationErrors);
        verifyAlignment("Mood", getJsonPropertyValues(PromptExtractionResult.Mood.class),
                rulesProperties.getMood(), validationErrors);
        verifyAlignment("PrimaryEnvironment", getEnumNames(PrimaryEnvironment.class),
                rulesProperties.getPrimaryEnvironment(), validationErrors);
        verifyAlignment("SweatLevel", getEnumNames(SweatLevel.class),
                rulesProperties.getSweatLevel(), validationErrors);
        verifyAlignment("VibePreference", getEnumNames(VibePreference.class),
                rulesProperties.getVibePreference(), validationErrors);
        verifyAlignment("Climate", getJsonPropertyValues(ClimateZone.class),
                rulesProperties.getClimate(), validationErrors);

        validateInteractionOverrides(validationErrors);

        if (!validationErrors.isEmpty()) {
            String errorSummary = String.join("\n - ", validationErrors);
            if (strictMode) {
                log.error("FATAL: Rules consistency check failed:\n - {}", errorSummary);
                throw new IllegalStateException("Startup aborted due to context-rules.yml mismatch:\n - " + errorSummary);
            } else {
                log.warn("WARNING: Rules consistency check detected mismatches:\n - {}", errorSummary);
            }
        }
    }

    private void validateInteractionOverrides(List<String> errors) {
        if (rulesProperties.getInteractionOverrides() == null) return;

        for (int i = 0; i < rulesProperties.getInteractionOverrides().size(); i++) {
            var override = rulesProperties.getInteractionOverrides().get(i);
            if (override.getWhen() != null) {
                Set<String> invalidKeys = new HashSet<>(override.getWhen().keySet());
                invalidKeys.removeAll(ALLOWED_DIMENSIONS);
                if (!invalidKeys.isEmpty()) {
                    errors.add(String.format("[interactionOverrides[%d]] Invalid dimension keys found in 'when': %s", i, invalidKeys));
                }
            }
        }
    }

    private void verifyAlignment(String category, Set<String> expectedKeys, Map<String, ?> yamlMap, List<String> errors) {
        Set<String> actualYamlKeys = yamlMap != null ? yamlMap.keySet() : Collections.emptySet();

        Set<String> missingInYaml = new HashSet<>(expectedKeys);
        missingInYaml.removeAll(actualYamlKeys);
        if (!missingInYaml.isEmpty()) {
            errors.add(String.format("[%s] Declared in Java but MISSING in YAML: %s", category, missingInYaml));
        }

        Set<String> unmappedInEnum = new HashSet<>(actualYamlKeys);
        unmappedInEnum.removeAll(expectedKeys);
        if (!unmappedInEnum.isEmpty()) {
            errors.add(String.format("[%s] Defined in YAML but UNMAPPED in Java: %s", category, unmappedInEnum));
        }
    }

    private <E extends Enum<E>> Set<String> getEnumNames(Class<E> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants()).map(Enum::name).collect(Collectors.toSet());
    }

    private <E extends Enum<E>> Set<String> getJsonPropertyValues(Class<E> enumClass) {
        // Reflection extraction logic remains unchanged
        Set<String> values = new HashSet<>();
        for (E constant : enumClass.getEnumConstants()) {
            try {
                Field field = enumClass.getField(constant.name());
                JsonProperty annotation = field.getAnnotation(JsonProperty.class);
                values.add((annotation != null && !annotation.value().isBlank()) ? annotation.value() : constant.name().toLowerCase());
            } catch (NoSuchFieldException e) {
                values.add(constant.name().toLowerCase());
            }
        }
        return values;
    }
}