package com.rasa.Rasa_be.modules.identity.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WardrobeReviewRequest {

    @NotNull(message = "Perceived sillage is required")
    @Min(0) @Max(100)
    private Integer perceivedSillage;

    @NotNull(message = "Perceived longevity is required")
    @Min(0) @Max(100)
    private Integer perceivedLongevity;
}