package com.rasa.Rasa_be.modules.identity.repository.projection;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CollectionItemProjection {
    UUID getInteractionId();
    UUID getPerfumeId();
    String getPerfumeName();
    String getBrandName();
    String getSource(); // Maps the Enum string
    Integer getPerceivedSillage();
    Integer getPerceivedLongevity();
    LocalDateTime getCreatedAt();
}