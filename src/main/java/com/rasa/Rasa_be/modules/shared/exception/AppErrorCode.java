package com.rasa.Rasa_be.modules.shared.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AppErrorCode {
    // Validation Errors
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "VAL_001", "Input validation failed"),

    // Auth & Security Errors
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH_001", "Authentication is required to access this resource"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH_002", "Invalid email or password"),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH_003", "The provided token has expired"),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "AUTH_004", "The provided token is invalid or malformed"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "AUTH_005", "You do not have permission to access this resource"),
    SESSION_REVOKED(HttpStatus.UNAUTHORIZED, "AUTH_006", "This session has been revoked"),

    // Business Logic Errors
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "BIZ_001", "Requested resource was not found"),
    RESOURCE_ALREADY_EXISTS(HttpStatus.CONFLICT, "BIZ_002", "Resource already exists"),
    BUSINESS_RULE_VIOLATION(HttpStatus.UNPROCESSABLE_ENTITY, "BIZ_003", "Business rule violation"),

    // System Errors
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SYS_500", "An unexpected system error occurred");

    private final HttpStatus httpStatus;
    private final String code;
    private final String defaultMessage;

    AppErrorCode(HttpStatus httpStatus, String code, String defaultMessage) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.defaultMessage = defaultMessage;
    }
}