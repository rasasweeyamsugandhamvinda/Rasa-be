package com.rasa.Rasa_be.modules.recommendation.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "recommendation.rules")
@Data
public class RecommendationRulesProperties {

    private int version;
    private String lastUpdated;

    private Map<String, RuleContext> occasion = new HashMap<>();
    private Map<String, RuleContext> mood = new HashMap<>();
    private Map<String, RuleContext> climate = new HashMap<>();
    private Map<String, RuleContext> primaryEnvironment = new HashMap<>();
    private Map<String, RuleContext> sweatLevel = new HashMap<>();
    private Map<String, RuleContext> vibePreference = new HashMap<>();

    private List<InteractionOverride> interactionOverrides = new ArrayList<>();

    @Data
    public static class RuleContext {
        private Map<String, Integer> accordDeltas = new HashMap<>();
        private TargetRange sillageTarget = new TargetRange();
        private TargetRange longevityTarget = new TargetRange();
        private String reason;
    }

    @Data
    public static class TargetRange {
        private Integer min;
        private Integer max;
    }

    @Data
    public static class InteractionOverride {
        private Map<String, String> when = new HashMap<>();
        private Double multiplySillageTargetMin;
        private String reason;
    }
}