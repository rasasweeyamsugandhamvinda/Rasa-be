package com.rasa.Rasa_be.modules.identity.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class PreferencesDto {

    private List<UUID> likedNoteIds;
    private List<UUID> dislikedNoteIds;
    private List<UUID> preferredAccordIds;
    private List<UUID> dislikedAccordIds;
}
