package com.rasa.Rasa_be.modules.recommendation.service;

import com.rasa.Rasa_be.modules.identity.dto.IdentityProfileDto;
import com.rasa.Rasa_be.modules.recommendation.dto.TargetScentProfile;

import java.util.List;

public interface TargetProfileGenerator {
    TargetScentProfile generate(IdentityProfileDto identity, String occasion, String mood, List<String> requestedAccords);
}
