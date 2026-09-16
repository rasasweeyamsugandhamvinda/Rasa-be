package com.rasa.Rasa_be.modules.recommendation.service.impl;

import com.rasa.Rasa_be.modules.identity.dto.IdentityProfileDto;
import com.rasa.Rasa_be.modules.recommendation.config.RecommendationRulesProperties;
import com.rasa.Rasa_be.modules.recommendation.dto.TargetScentProfile;
import com.rasa.Rasa_be.modules.recommendation.service.TargetProfileGenerator;
import com.rasa.Rasa_be.modules.recommendation.utility.ClimateResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class TargetProfileGeneratorImpl implements TargetProfileGenerator {

    private final RecommendationRulesProperties rules;
    private final ClimateResolver climateResolver;

    @Override
    public TargetScentProfile generate(IdentityProfileDto identity, String occasion, String mood, List<String> requestedAccords) {
        Map<String, String> activeContext = new HashMap<>();

        // HIERARCHY RULE 1: Real-time Occasion overrides Baseline Environment
        if (occasion != null) {
            activeContext.put("occasion", occasion);
        } else if (identity.getPrimaryEnvironment() != null) {
            activeContext.put("primaryEnvironment", identity.getPrimaryEnvironment().name());
        }

        // HIERARCHY RULE 2: Real-time Mood overrides Baseline Vibe Preference
        if (mood != null) {
            activeContext.put("mood", mood);
        } else if (identity.getVibePreference() != null) {
            activeContext.put("vibePreference", identity.getVibePreference().name());
        }

        // Sweat level remains independent of occasion/mood
        if (identity.getSweatLevel() != null) {
            activeContext.put("sweatLevel", identity.getSweatLevel().name());
        }

        // The single source of truth for climate resolution
        if (identity.getCity() != null || identity.getState() != null) {
            String climateBucket = climateResolver.resolveClimate(identity.getCity(), identity.getState());
            activeContext.put("climate", climateBucket);
        }

        TargetScentProfile profile = new TargetScentProfile();
        applyBaseRules(activeContext, profile);
        applyOverrides(activeContext, profile);

        // --- DIRECT DESCRIPTOR BOOST ---
        // Overrides or heavily compounds weights for exact user keywords like "sweet" or "leather"
        if (requestedAccords != null && !requestedAccords.isEmpty()) {
            for (String accord : requestedAccords) {
                String normalizedAccord = accord.toLowerCase().replace(" ", "_");

                // Inject a massive +50 delta to force matching candidates to the top of the curve
                int currentDelta = profile.getAccordDeltas().getOrDefault(normalizedAccord, 0);
                profile.getAccordDeltas().put(normalizedAccord, currentDelta + 50);

                profile.getExplanationReasons().add("specifically matches your request for a '" + accord + "' profile");
            }
        }

        return profile;
    }

    private void applyBaseRules(Map<String, String> context, TargetScentProfile profile) {
        applyIfPresent(context, "occasion", rules.getOccasion(), profile);
        applyIfPresent(context, "mood", rules.getMood(), profile);
        applyIfPresent(context, "primaryEnvironment", rules.getPrimaryEnvironment(), profile);
        applyIfPresent(context, "sweatLevel", rules.getSweatLevel(), profile);
        applyIfPresent(context, "vibePreference", rules.getVibePreference(), profile);
        applyIfPresent(context, "climate", rules.getClimate(), profile);
    }

    private void applyIfPresent(Map<String, String> context, String key,
                                Map<String, RecommendationRulesProperties.RuleContext> ruleMap,
                                TargetScentProfile profile) {
        String value = context.get(key);
        if (value == null || ruleMap == null) return;
        RecommendationRulesProperties.RuleContext rule = ruleMap.get(value);
        if (rule == null) {
            log.debug("No rule found for {}={}", key, value);
            return;
        }
        applyRule(profile, rule);
    }

    private void applyRule(TargetScentProfile target, RecommendationRulesProperties.RuleContext rule) {
        if (rule.getAccordDeltas() != null) {
            rule.getAccordDeltas().forEach(target::addAccordDelta);
        }
        if (rule.getSillageTarget() != null) {
            if (rule.getSillageTarget().getMin() != null)
                target.setMinSillage(target.getMinSillage() == null ? rule.getSillageTarget().getMin() : Math.max(target.getMinSillage(), rule.getSillageTarget().getMin()));
            if (rule.getSillageTarget().getMax() != null)
                target.setMaxSillage(target.getMaxSillage() == null ? rule.getSillageTarget().getMax() : Math.min(target.getMaxSillage(), rule.getSillageTarget().getMax()));
        }
        if (rule.getLongevityTarget() != null) {
            if (rule.getLongevityTarget().getMin() != null)
                target.setMinLongevity(target.getMinLongevity() == null ? rule.getLongevityTarget().getMin() : Math.max(target.getMinLongevity(), rule.getLongevityTarget().getMin()));
            if (rule.getLongevityTarget().getMax() != null)
                target.setMaxLongevity(target.getMaxLongevity() == null ? rule.getLongevityTarget().getMax() : Math.min(target.getMaxLongevity(), rule.getLongevityTarget().getMax()));
        }
        if (rule.getReason() != null) target.getExplanationReasons().add(rule.getReason());

        if (target.getMinSillage() != null && target.getMaxSillage() != null && target.getMinSillage() > target.getMaxSillage())
            log.warn("Conflicting sillage target after merge: min={} > max={}", target.getMinSillage(), target.getMaxSillage());
        if (target.getMinLongevity() != null && target.getMaxLongevity() != null && target.getMinLongevity() > target.getMaxLongevity())
            log.warn("Conflicting longevity target after merge: min={} > max={}", target.getMinLongevity(), target.getMaxLongevity());
    }

    private void applyOverrides(Map<String, String> context, TargetScentProfile profile) {
        if (rules.getInteractionOverrides() == null) return;
        for (var override : rules.getInteractionOverrides()) {
            boolean matches = override.getWhen().entrySet().stream()
                    .allMatch(e -> e.getValue().equals(context.get(e.getKey())));
            if (matches) {
                if (override.getMultiplySillageTargetMin() != null && profile.getMinSillage() != null) {
                    profile.setMinSillage((int) Math.round(profile.getMinSillage() * override.getMultiplySillageTargetMin()));
                }
                if (override.getReason() != null) profile.getExplanationReasons().add(override.getReason());
            }
        }
    }
}