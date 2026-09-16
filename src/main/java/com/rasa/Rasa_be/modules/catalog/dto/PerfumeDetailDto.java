package com.rasa.Rasa_be.modules.catalog.dto;

import com.rasa.Rasa_be.modules.catalog.entity.enums.*;
import lombok.*;
import java.util.Set;
import java.util.UUID;

@Data @AllArgsConstructor @NoArgsConstructor
public class PerfumeDetailDto {
    private UUID id;
    private String name;
    private String brandName;
    private String brandCountry;
    private Gender gender;
    private Concentration concentration;
    private PriceTier priceTier;
    private Integer longevity;
    private Integer sillage;
    private String imageUrl;
    private Set<NoteDto> notes;
    private Set<AccordDto> accords;
}
