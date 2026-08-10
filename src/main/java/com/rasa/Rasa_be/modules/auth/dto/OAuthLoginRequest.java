package com.rasa.Rasa_be.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record OAuthLoginRequest(
        @NotBlank(message = "ID Token is required")
        String idToken
) {}