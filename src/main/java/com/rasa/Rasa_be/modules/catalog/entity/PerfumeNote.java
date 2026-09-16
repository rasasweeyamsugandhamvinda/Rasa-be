package com.rasa.Rasa_be.modules.catalog.entity;

import com.rasa.Rasa_be.modules.shared.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "perfume_notes", schema = "catalog")
@Getter @Setter @NoArgsConstructor
public class PerfumeNote extends BaseAuditEntity {
    @EmbeddedId
    private PerfumeNoteId id = new PerfumeNoteId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("perfumeId")
    @JoinColumn(name = "perfume_id")
    private Perfume perfume;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("noteId")
    @JoinColumn(name = "note_id")
    private Note note;

}
