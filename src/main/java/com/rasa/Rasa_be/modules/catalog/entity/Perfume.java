package com.rasa.Rasa_be.modules.catalog.entity;

import com.rasa.Rasa_be.modules.catalog.entity.enums.*;
import com.rasa.Rasa_be.modules.shared.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "perfumes", schema = "catalog")
@Getter @Setter @NoArgsConstructor
public class Perfume extends BaseAuditEntity {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    private String name;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private Concentration concentration;

    @Enumerated(EnumType.STRING)
    @Column(name = "price_tier")
    private PriceTier priceTier;

    private Integer longevity;
    private Integer sillage;
    @Column(name = "image_url")
    private String imageUrl;
    
    private String source;
    @Column(name = "source_url")
    private String sourceUrl;
    @Column(name = "is_verified")
    private Boolean isVerified;

    @OneToMany(mappedBy = "perfume", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PerfumeNote> notes = new HashSet<>();

    @OneToMany(mappedBy = "perfume", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PerfumeAccord> accords = new HashSet<>();

}
