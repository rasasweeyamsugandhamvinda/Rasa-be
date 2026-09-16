package com.rasa.Rasa_be.modules.catalog.entity;

import com.rasa.Rasa_be.modules.catalog.entity.enums.NoteType;
import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode
public class PerfumeNoteId implements Serializable {
    private UUID perfumeId;
    private UUID noteId;
    @Enumerated(EnumType.STRING)
    @Column(name = "note_type")
    private NoteType noteType;
}
