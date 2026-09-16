package com.rasa.Rasa_be.modules.recommendation.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class ScoredPerfumeDto {
    private UUID perfumeId;
    private String name;
    private String brandName;
    private double finalScore;
    private Map<String, Double> scoreBreakdown;
    private List<String> explanationReasons;
}