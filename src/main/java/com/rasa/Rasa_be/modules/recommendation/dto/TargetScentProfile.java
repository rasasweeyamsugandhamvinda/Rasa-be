package com.rasa.Rasa_be.modules.recommendation.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class TargetScentProfile {
    private Map<String, Integer> accordDeltas = new HashMap<>();
    private Integer minSillage;
    private Integer maxSillage;
    private Integer minLongevity;
    private Integer maxLongevity;
    private List<String> explanationReasons = new ArrayList<>();

    public void addAccordDelta(String accord, Integer value) {
        accordDeltas.merge(accord, value, Integer::sum);
    }
}