package com.rasa.Rasa_be.modules.catalog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerfumeCandidateDto {
    private UUID perfumeId;
    private String name;
    private String brandName;
    private Map<UUID, String> noteMap;   // Changed from Set<UUID> to Map<UUID, String>
    private Map<UUID, String> accordMap; // Changed from Set<UUID> to Map<UUID, String>
    private Integer baseLongevity;
    private Integer baseSillage;
}