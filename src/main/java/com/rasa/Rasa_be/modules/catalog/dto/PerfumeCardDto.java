package com.rasa.Rasa_be.modules.catalog.dto;

import com.rasa.Rasa_be.modules.catalog.entity.enums.*;
import lombok.*;
import java.util.UUID;

@Data @AllArgsConstructor @NoArgsConstructor
public class PerfumeCardDto {
    private UUID id;
    private String name;
    private String brandName;
    private Gender gender;
    private Concentration concentration;
    private String imageUrl;
    private PriceTier priceTier;
}
