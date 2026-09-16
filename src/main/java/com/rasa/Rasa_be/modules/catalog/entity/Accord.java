package com.rasa.Rasa_be.modules.catalog.entity;

import com.rasa.Rasa_be.modules.shared.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "accords", schema = "catalog")
@Getter @Setter @NoArgsConstructor
public class Accord extends BaseAuditEntity {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
}
