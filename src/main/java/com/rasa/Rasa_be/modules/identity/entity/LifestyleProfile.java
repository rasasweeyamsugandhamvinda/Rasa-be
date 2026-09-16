package com.rasa.Rasa_be.modules.identity.entity;

import com.rasa.Rasa_be.modules.identity.entity.enums.BudgetPreference;
import com.rasa.Rasa_be.modules.identity.entity.enums.PrimaryEnvironment;
import com.rasa.Rasa_be.modules.identity.entity.enums.SweatLevel;
import com.rasa.Rasa_be.modules.identity.entity.enums.VibePreference;
import com.rasa.Rasa_be.modules.shared.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

@Entity
@Table(name = "lifestyle_profiles", schema = "identity")
@Getter @Setter @NoArgsConstructor
public class LifestyleProfile extends BaseAuditEntity {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    private String city;
    private String state;

    @Enumerated(EnumType.STRING)
    @Column(name = "primary_environment")
    private PrimaryEnvironment primaryEnvironment;

    @Enumerated(EnumType.STRING)
    @Column(name = "sweat_level")
    private SweatLevel sweatLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "vibe_preference")
    private VibePreference vibePreference;

    @Enumerated(EnumType.STRING)
    @Column(name = "budget_preference")
    private BudgetPreference budgetPreference;

}
