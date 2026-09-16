package com.rasa.Rasa_be.modules.catalog.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Data @AllArgsConstructor @NoArgsConstructor
public class AccordDto {
    private UUID id;
    private String name;
    private BigDecimal weight;
}
