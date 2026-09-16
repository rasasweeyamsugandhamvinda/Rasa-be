package com.rasa.Rasa_be.modules.recommendation.service.impl;

import com.rasa.Rasa_be.modules.catalog.dto.CandidateFilterRequest;
import com.rasa.Rasa_be.modules.catalog.dto.PerfumeCandidateDto;
import com.rasa.Rasa_be.modules.catalog.entity.enums.PriceTier;
import com.rasa.Rasa_be.modules.catalog.service.CatalogService;
import com.rasa.Rasa_be.modules.identity.dto.IdentityProfileDto;
import com.rasa.Rasa_be.modules.identity.entity.enums.BudgetPreference;
import com.rasa.Rasa_be.modules.identity.entity.enums.InteractionSource;
import com.rasa.Rasa_be.modules.identity.entity.enums.SignalType;
import com.rasa.Rasa_be.modules.identity.repository.InteractionRepository;
import com.rasa.Rasa_be.modules.identity.repository.projection.AccordAffinity;
import com.rasa.Rasa_be.modules.identity.service.IdentityService;
import com.rasa.Rasa_be.modules.recommendation.dto.RecommendationRequestDto;
import com.rasa.Rasa_be.modules.recommendation.dto.ScoredPerfumeDto;
import com.rasa.Rasa_be.modules.recommendation.dto.TargetScentProfile;
import com.rasa.Rasa_be.modules.recommendation.service.RecommendationService;
import com.rasa.Rasa_be.modules.recommendation.service.TargetProfileGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationServiceImpl implements RecommendationService {

    private final IdentityService identityService;
    private final CatalogService catalogService;
    private final TargetProfileGenerator targetProfileGenerator;
    private final InteractionRepository interactionRepository;

    private static final double K_EXPLICIT_CONFIDENCE = 5.0;
    private static final double W_CONTEXT = 1.0;

    @Override
    public List<ScoredPerfumeDto> generateRecommendations(UUID userId, RecommendationRequestDto request) {
        log.debug("Generating recommendations for user: {}", userId);

        IdentityProfileDto identity = identityService.getUserIdentityProfile(userId);

        if (request.getEnvironmentOverride() != null) {
            identity.setPrimaryEnvironment(request.getEnvironmentOverride());
        }
        if (request.getSweatLevelOverride() != null) {
            identity.setSweatLevel(request.getSweatLevelOverride());
        }
        if (request.getVibePreferenceOverride() != null) {
            identity.setVibePreference(request.getVibePreferenceOverride());
        }

        TargetScentProfile targetProfile = targetProfileGenerator.generate(identity, request.getOccasion(), request.getMood(), request.getRequestedAccords());

        BudgetPreference effectiveBudget = request.getBudgetOverride() != null
                ? request.getBudgetOverride()
                : identity.getBudgetPreference();

        PriceTier mappedPriceTier;
        try {
            mappedPriceTier = effectiveBudget != null ? PriceTier.valueOf(effectiveBudget.name()) : null;
        } catch (IllegalArgumentException e) {
            log.error("BudgetPreference '{}' has no matching PriceTier — proceeding without budget filter", effectiveBudget, e);
            mappedPriceTier = null;
        }

        CandidateFilterRequest filterRequest = CandidateFilterRequest.builder()
                .maxPriceTier(mappedPriceTier)
                .excludedNoteIds(identity.getDislikedNoteIds())
                .excludedAccordIds(identity.getDislikedAccordIds())
                .build();

        List<PerfumeCandidateDto> candidates = catalogService.getRecommendationCandidates(filterRequest);
        if (candidates.isEmpty()) {
            return Collections.emptyList();
        }

        // --- STEP 1: CALCULATE RAW SCORES & FIND THE MAXIMUM CURVE ---
        Map<UUID, Double> rawContextScores = new HashMap<>();
        double maxRawScore = 1.0; // Set to 1.0 to prevent divide-by-zero later

        for (PerfumeCandidateDto candidate : candidates) {
            double rawScore = calculateRawContextScore(candidate, targetProfile);
            rawContextScores.put(candidate.getPerfumeId(), rawScore);
            maxRawScore = Math.max(maxRawScore, rawScore);
        }

        // --- STEP 2: FETCH HISTORY SIGNALS ---
        long wardrobeCount = interactionRepository.countByUserIdAndSourceAndSignalType(
                userId, InteractionSource.POST_USE_REVIEW, SignalType.POSITIVE);
        List<AccordAffinity> implicitAffinities = interactionRepository.findImplicitAccordAffinities(userId);

        int explicitSignalCount = identity.getLikedNoteIds().size() +
                identity.getPreferredAccordIds().size() +
                identity.getDislikedNoteIds().size() +
                identity.getDislikedAccordIds().size();

        double wExplicit = Math.min(1.0, explicitSignalCount / K_EXPLICIT_CONFIDENCE);
        double wContext = W_CONTEXT;

        final double finalMaxRaw = maxRawScore; // Required for use inside the lambda

        // --- STEP 3: STREAM & NORMALIZE SCORES ---
        return candidates.stream()
                .map(candidate -> {
                    // Grade this candidate against the best candidate in the DB
                    double rawScore = rawContextScores.get(candidate.getPerfumeId());
                    double normalizedContextScore = (rawScore / finalMaxRaw) * 100.0;

                    return scoreCandidate(
                            candidate, identity, targetProfile, implicitAffinities,
                            wardrobeCount, wExplicit, wContext, normalizedContextScore // Passed in directly
                    );
                })
                .sorted(Comparator.comparingDouble(ScoredPerfumeDto::getFinalScore).reversed()
                        .thenComparing(ScoredPerfumeDto::getName))
                .limit(20)
                .collect(Collectors.toList());
    }

    private ScoredPerfumeDto scoreCandidate(PerfumeCandidateDto candidate,
                                            IdentityProfileDto identity,
                                            TargetScentProfile targetProfile,
                                            List<AccordAffinity> implicitAffinities,
                                            long wardrobeCount,
                                            double wExplicit,
                                            double wContext,
                                            double normalizedContextScore) { // <--- ADDED AS PARAMETER

        ExplicitResult explicitResult = calculateExplicitScore(candidate, identity);

        // Removed the internal calculateContextScore call from here!

        double historyScore = 0.0;
        double wHistory = wardrobeCount == 0 ? 0.0 : Math.min(0.5, wardrobeCount / 10.0);

        if (wHistory > 0.0 && !implicitAffinities.isEmpty()) {
            double scoreSum = 0.0;
            double maxAffinityWeight = implicitAffinities.get(0).getTotalWeight();

            if (candidate.getAccordMap() != null) {
                for (Map.Entry<UUID, String> candidateAccord : candidate.getAccordMap().entrySet()) {
                    double affinityWeight = getAffinityWeight(implicitAffinities, candidateAccord.getKey());
                    if (affinityWeight > 0) {
                        double normalizedAffinity = affinityWeight / maxAffinityWeight;
                        scoreSum += normalizedAffinity * 33.3;
                    }
                }
            }
            historyScore = Math.min(100.0, scoreSum);
        }

        double totalWeight = wExplicit + wContext + wHistory;
        double finalScore = ((explicitResult.score() * wExplicit) +
                (normalizedContextScore * wContext) +
                (historyScore * wHistory)) / totalWeight;

        Map<String, Double> breakdown = new HashMap<>();
        breakdown.put("explicitScore", explicitResult.score());
        breakdown.put("contextScore", normalizedContextScore); // Use the normalized one
        breakdown.put("historyScore", historyScore);
        breakdown.put("explicitWeight", wExplicit);
        breakdown.put("contextWeight", wContext);
        breakdown.put("historyWeight", wHistory);

        List<String> combinedReasons = new ArrayList<>(targetProfile.getExplanationReasons());
        combinedReasons.addAll(explicitResult.matchedReasons());

        return ScoredPerfumeDto.builder()
                .perfumeId(candidate.getPerfumeId())
                .name(candidate.getName())
                .brandName(candidate.getBrandName())
                .finalScore(Math.round(finalScore * 100.0) / 100.0)
                .scoreBreakdown(breakdown)
                .explanationReasons(combinedReasons)
                .build();
    }

    private ExplicitResult calculateExplicitScore(PerfumeCandidateDto candidate, IdentityProfileDto identity) {
        if (identity.getLikedNoteIds().isEmpty() && identity.getPreferredAccordIds().isEmpty()) {
            return new ExplicitResult(0.0, List.of());
        }

        List<String> dynamicReasons = new ArrayList<>();
        long matchedSignals = 0;

        for (UUID likedNoteId : identity.getLikedNoteIds()) {
            if (candidate.getNoteMap().containsKey(likedNoteId)) {
                matchedSignals++;
                dynamicReasons.add("Features your liked note: " + candidate.getNoteMap().get(likedNoteId));
            }
        }

        for (UUID preferredAccordId : identity.getPreferredAccordIds()) {
            if (candidate.getAccordMap().containsKey(preferredAccordId)) {
                matchedSignals++;
                dynamicReasons.add("Matches your preferred accord: " + candidate.getAccordMap().get(preferredAccordId));
            }
        }

        double score = Math.min(100.0, matchedSignals * 25.0);

        return new ExplicitResult(score, dynamicReasons);
    }

    private double calculateRawContextScore(PerfumeCandidateDto candidate, TargetScentProfile target) {
        double score = 0.0;

        if (candidate.getAccordMap() != null) {
            for (String accordName : candidate.getAccordMap().values()) {
                String normalizedAccord = accordName.toLowerCase().replace(" ", "_");
                score += target.getAccordDeltas().getOrDefault(normalizedAccord, 0);
            }
        }

        if (target.getMinSillage() != null && candidate.getBaseSillage() != null && candidate.getBaseSillage() < target.getMinSillage()) score -= 15.0;
        if (target.getMaxSillage() != null && candidate.getBaseSillage() != null && candidate.getBaseSillage() > target.getMaxSillage()) score -= 15.0;
        if (target.getMinLongevity() != null && candidate.getBaseLongevity() != null && candidate.getBaseLongevity() < target.getMinLongevity()) score -= 15.0;
        if (target.getMaxLongevity() != null && candidate.getBaseLongevity() != null && candidate.getBaseLongevity() > target.getMaxLongevity()) score -= 15.0;

        return Math.max(0.0, score);
    }

    private double getAffinityWeight(List<AccordAffinity> affinities, UUID accordId) {
        if (accordId == null) return 0.0;

        return affinities.stream()
                .filter(a -> accordId.equals(a.getAccordId()))
                .map(AccordAffinity::getTotalWeight)
                .findFirst()
                .orElse(0.0);
    }

    private record ExplicitResult(double score, List<String> matchedReasons) {}
}