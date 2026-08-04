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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException, ServletException {

        log.warn("Access denied attempt to {}: {}", request.getRequestURI(), accessDeniedException.getMessage());

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        ApiResponse<Void> apiResponse = ApiResponse.error(
                HttpStatus.FORBIDDEN,
                "You do not have permission to access this resource.",
                Map.of("errorCode", AppErrorCode.ACCESS_DENIED.getCode())
        );

        objectMapper.writeValue(response.getOutputStream(), apiResponse);
    }
}