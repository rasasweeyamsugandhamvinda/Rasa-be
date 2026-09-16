package com.rasa.Rasa_be.modules.recommendation.service;

import com.rasa.Rasa_be.modules.recommendation.dto.RecommendationRequestDto;
import com.rasa.Rasa_be.modules.recommendation.dto.ScoredPerfumeDto;

import java.util.List;
import java.util.UUID;

public interface RecommendationService {
    List<ScoredPerfumeDto> generateRecommendations(UUID userId, RecommendationRequestDto request);
}
