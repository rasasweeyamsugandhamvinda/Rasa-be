package com.rasa.Rasa_be.modules.identity.repository.projection;

import java.util.UUID;

public interface AccordAffinity {
    UUID getAccordId();
    Double getTotalWeight();
}