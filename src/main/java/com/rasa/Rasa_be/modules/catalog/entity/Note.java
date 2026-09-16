package com.rasa.Rasa_be.modules.catalog.entity;

import com.rasa.Rasa_be.modules.shared.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "notes", schema = "catalog")
@Getter @Setter @NoArgsConstructor
public class Note extends BaseAuditEntity {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    @Column(name = "is_canonical", nullable = false)
    private boolean isCanonical = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "canonical_id")
    private Note canonicalParent;
}
