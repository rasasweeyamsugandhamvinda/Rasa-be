package com.rasa.Rasa_be.modules.auth.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserSessionResponse(
        UUID sessionId,
        String deviceInfo,
        String ipAddress,
        LocalDateTime createdAt,
        LocalDateTime expiresAt
) {}