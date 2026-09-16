package com.rasa.Rasa_be.modules.identity.entity;

import com.rasa.Rasa_be.modules.identity.entity.enums.ExperienceLevel;
import com.rasa.Rasa_be.modules.shared.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

@Entity
@Table(name = "user_profiles", schema = "identity")
@Getter @Setter @NoArgsConstructor
public class UserProfile extends BaseAuditEntity {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "experience_level")
    private ExperienceLevel experienceLevel;

    @Column(name = "onboarding_completed", nullable = false)
    private Boolean onboardingCompleted = false;


}
