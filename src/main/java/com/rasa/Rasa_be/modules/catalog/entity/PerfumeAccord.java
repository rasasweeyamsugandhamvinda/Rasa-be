package com.rasa.Rasa_be.modules.catalog.entity;

import com.rasa.Rasa_be.modules.shared.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "perfume_accords", schema = "catalog")
@Getter @Setter @NoArgsConstructor
public class PerfumeAccord extends BaseAuditEntity {
    @EmbeddedId
    private PerfumeAccordId id = new PerfumeAccordId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("perfumeId")
    @JoinColumn(name = "perfume_id")
    private Perfume perfume;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("accordId")
    @JoinColumn(name = "accord_id")
    private Accord accord;

    private BigDecimal weight;
}
