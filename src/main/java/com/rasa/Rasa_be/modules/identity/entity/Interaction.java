package com.rasa.Rasa_be.modules.identity.entity;

import com.rasa.Rasa_be.modules.identity.entity.enums.InteractionSource;
import com.rasa.Rasa_be.modules.identity.entity.enums.SignalType;
import com.rasa.Rasa_be.modules.shared.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "interactions", schema = "identity")
@Getter @Setter @NoArgsConstructor
public class Interaction extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserProfile userProfile;

    @Column(name = "perfume_id", nullable = false)
    private UUID perfumeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false)
    private InteractionSource source;

    @Enumerated(EnumType.STRING)
    @Column(name = "signal_type", nullable = false)
    private SignalType signalType;

    private Integer rating;

    @Column(name = "perceived_sillage")
    private Integer perceivedSillage;

    @Column(name = "perceived_longevity")
    private Integer perceivedLongevity;
}
