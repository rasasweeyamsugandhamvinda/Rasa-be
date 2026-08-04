package com.rasa.Rasa_be.modules.shared.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;
    private final int code;
    private final String status;
    private final String message;
    private final T data;
    private final Object error;
    private final Object metadata;

    @Builder.Default
    private final LocalDateTime timestamp = LocalDateTime.now();

    public static <T> ApiResponse<T> success(HttpStatus httpStatus, T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .code(httpStatus.value())
                .status(httpStatus.name())
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(HttpStatus httpStatus, String message, Object errorDetails) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(httpStatus.value())
                .status(httpStatus.name())
                .message(message)
                .error(errorDetails)
                .build();
    }
}