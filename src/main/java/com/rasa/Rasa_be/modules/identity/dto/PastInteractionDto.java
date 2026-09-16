package com.rasa.Rasa_be.modules.identity.dto;

import com.rasa.Rasa_be.modules.identity.entity.enums.SignalType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class PastInteractionDto {

    @NotNull(message = "Perfume ID is required")
    private UUID perfumeId;

    @NotNull(message = "Signal type is required")
    private SignalType signalType;
}
