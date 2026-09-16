package com.rasa.Rasa_be.modules.identity.service;

import com.rasa.Rasa_be.modules.identity.repository.projection.CollectionItemProjection;

import java.util.List;
import java.util.UUID;

public interface InteractionService {
    void addToLibrary(UUID userId, UUID perfumeId);
    void moveToWardrobe(UUID userId, UUID perfumeId, int perceivedSillage, int perceivedLongevity);
    List<CollectionItemProjection> getUserLibrary(UUID userId);
    List<CollectionItemProjection> getUserWardrobe(UUID userId);
    public void removeInteraction(UUID userId, UUID perfumeId);
}
