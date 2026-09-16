package com.rasa.Rasa_be.modules.catalog.dto;

import com.rasa.Rasa_be.modules.catalog.entity.enums.NoteType;
import lombok.*;
import java.util.UUID;

@Data @AllArgsConstructor @NoArgsConstructor
public class NoteDto {
    private UUID id;
    private String name;
    private NoteType type;
}
