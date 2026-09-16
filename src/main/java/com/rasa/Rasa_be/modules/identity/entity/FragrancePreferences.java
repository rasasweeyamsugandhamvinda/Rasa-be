package com.rasa.Rasa_be.modules.identity.entity;

import com.rasa.Rasa_be.modules.shared.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

@Entity
@Table(name = "fragrance_preferences", schema = "identity")
@Getter @Setter @NoArgsConstructor
public class FragrancePreferences extends BaseAuditEntity {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "liked_note_ids", columnDefinition = "uuid[]")
    private UUID[] likedNoteIds;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "disliked_note_ids", columnDefinition = "uuid[]")
    private UUID[] dislikedNoteIds;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "preferred_accord_ids", columnDefinition = "uuid[]")
    private UUID[] preferredAccordIds;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "disliked_accord_ids", columnDefinition = "uuid[]")
    private UUID[] dislikedAccordIds;

}
