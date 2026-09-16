package com.rasa.Rasa_be.modules.recommendation.service.impl;

import com.rasa.Rasa_be.modules.recommendation.dto.PromptExtractionResult;
import com.rasa.Rasa_be.modules.recommendation.dto.RecommendationRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PromptParsingService {

    private final ChatClient chatClient;

    public PromptParsingService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public void parseAndEnrich(RecommendationRequestDto request) {

        if (request.getPrompt() == null || request.getPrompt().isBlank()) {
            return;
        }

        var converter = new BeanOutputConverter<>(PromptExtractionResult.class);

        String systemPrompt = """
            You are an intent extraction engine for a perfume recommendation API.

            Extract the user's intent and map it to the target JSON schema.
            If a field is not explicitly or implicitly mentioned, leave it null.

            vibePreferenceOverride guidance:
            Infer this from the emotional tone/occasion of the prompt even if not
            stated in perfume-vocabulary terms.
            - "sweet warm romantic date night" -> SEDUCTIVE
            - "clean, professional, understated" -> PROFESSIONAL
            - "everyday light and easy" -> FRESH_CASUAL
            - "bold, make people notice me" -> LOUD_ATTENTION_GRABBING

            requestedAccords guidance:
            Extract any explicitly requested smells, notes, or fragrance families as a list of strings.
            - E.g., if prompt is "sweet leathery date night perfume", extract ["sweet", "leather"]
            - E.g., if prompt is "fresh citrus for summer", extract ["fresh", "citrus"]
            Normalize to lowercase.

            Only set vibePreferenceOverride when the prompt gives a real signal.
            For enum values, use the exact enum constant names expected by the schema.

            {format}
            """;

        try {

            PromptExtractionResult extracted = chatClient.prompt()
                    .system(s -> s
                            .text(systemPrompt)
                            .param("format", converter.getFormat()))
                    .user(request.getPrompt())
                    .call()
                    .entity(converter);

            if (extracted != null) {

                if (extracted.occasion() != null) {
                    request.setOccasion(
                            extracted.occasion()
                                    .name()
                                    .toLowerCase()
                    );
                }

                if (extracted.mood() != null) {
                    request.setMood(
                            extracted.mood()
                                    .name()
                                    .toLowerCase()
                    );
                }

                if (extracted.environmentOverride() != null) {
                    request.setEnvironmentOverride(
                            extracted.environmentOverride()
                    );
                }

                if (extracted.sweatLevelOverride() != null) {
                    request.setSweatLevelOverride(
                            extracted.sweatLevelOverride()
                    );
                }

                if (extracted.budgetOverride() != null) {
                    request.setBudgetOverride(
                            extracted.budgetOverride()
                    );
                }

                if (extracted.vibePreferenceOverride() != null) {
                    request.setVibePreferenceOverride(
                            extracted.vibePreferenceOverride()
                    );
                }

                // NEW: Map the explicitly requested accords
                if (extracted.requestedAccords() != null && !extracted.requestedAccords().isEmpty()) {
                    request.setRequestedAccords(extracted.requestedAccords());
                }
            }

        } catch (Exception e) {
            log.warn(
                    "Extraction failed or schema violated for prompt: '{}'. " +
                            "Proceeding with baseline profile. Error: {}",
                    request.getPrompt(),
                    e.getMessage()
            );
        }
    }
}