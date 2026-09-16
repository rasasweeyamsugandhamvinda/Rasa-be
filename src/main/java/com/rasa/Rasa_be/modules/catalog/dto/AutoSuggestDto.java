package com.rasa.Rasa_be.modules.catalog.dto;

import lombok.*;
import java.util.UUID;

@Data @AllArgsConstructor @NoArgsConstructor
public class AutoSuggestDto {
    private UUID perfumeId;
    private String perfumeName;
    private String brandName;
}
