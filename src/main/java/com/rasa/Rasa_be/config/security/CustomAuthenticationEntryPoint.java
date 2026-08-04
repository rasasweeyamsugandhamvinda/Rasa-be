package com.rasa.Rasa_be.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rasa.Rasa_be.modules.shared.dto.ApiResponse;
import com.rasa.Rasa_be.modules.shared.exception.AppErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {

        log.warn("Unauthorized access attempt to {}: {}", request.getRequestURI(), authException.getMessage());

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ApiResponse<Void> apiResponse = ApiResponse.error(
                HttpStatus.UNAUTHORIZED,
                "Authentication is required. Token may be missing, invalid, or expired.",
                Map.of("errorCode", AppErrorCode.UNAUTHORIZED.getCode())
        );

        // Manually write our standard ApiResponse into the HTTP response stream
        objectMapper.writeValue(response.getOutputStream(), apiResponse);
    }
}