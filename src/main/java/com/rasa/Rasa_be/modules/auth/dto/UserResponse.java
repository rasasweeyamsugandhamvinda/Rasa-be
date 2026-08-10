package com.rasa.Rasa_be.modules.auth.dto;

import com.rasa.Rasa_be.modules.auth.domain.AuthProvider;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        boolean isVerified,
        AuthProvider authProvider,
        LocalDateTime createdAt
) {}
