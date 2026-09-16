package com.rasa.Rasa_be.modules.recommendation.controller;

import com.rasa.Rasa_be.config.security.UserPrincipal;
import com.rasa.Rasa_be.modules.recommendation.dto.RecommendationRequestDto;
import com.rasa.Rasa_be.modules.recommendation.dto.ScoredPerfumeDto;
import com.rasa.Rasa_be.modules.recommendation.service.RecommendationService;
import com.rasa.Rasa_be.modules.recommendation.service.impl.PromptParsingService;
import com.rasa.Rasa_be.modules.shared.dto.ApiResponse;
import com.rasa.Rasa_be.modules.shared.exception.AppErrorCode;
import com.rasa.Rasa_be.modules.shared.exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
@Slf4j
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final PromptParsingService promptParsingService;

    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<List<ScoredPerfumeDto>>> generate(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody(required = false) RecommendationRequestDto request) {

        if (principal == null) {
            throw new AppException(AppErrorCode.UNAUTHORIZED, "Authentication required to generate recommendations.");
        }

        RecommendationRequestDto safeRequest = request != null ? request : new RecommendationRequestDto();

        log.info("Recommendation request received for user: {}", principal.id());

        promptParsingService.parseAndEnrich(safeRequest);

        List<ScoredPerfumeDto> recommendations = recommendationService.generateRecommendations(principal.id(), safeRequest);

        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                recommendations,
                "Recommendations generated successfully."
        ));
    }
}