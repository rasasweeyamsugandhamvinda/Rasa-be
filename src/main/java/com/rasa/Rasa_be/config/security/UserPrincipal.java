package com.rasa.Rasa_be.config.security;

import java.util.UUID;

public record UserPrincipal(
        UUID id,
        String email
) {}