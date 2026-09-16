package com.rasa.Rasa_be.modules.catalog.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode
public class PerfumeAccordId implements Serializable {
    private UUID perfumeId;
    private UUID accordId;
}
