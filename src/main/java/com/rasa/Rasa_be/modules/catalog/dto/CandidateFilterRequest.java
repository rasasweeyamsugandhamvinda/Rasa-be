package com.rasa.Rasa_be.modules.catalog.dto;

import com.rasa.Rasa_be.modules.catalog.entity.enums.PriceTier;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class CandidateFilterRequest {
    private PriceTier maxPriceTier;
    private List<UUID> excludedNoteIds;
    private List<UUID> excludedAccordIds;
}