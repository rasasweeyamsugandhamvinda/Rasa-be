package com.rasa.Rasa_be.modules.shared.exception;

import com.rasa.Rasa_be.modules.shared.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Void>> handleAppException(AppException ex) {
        log.warn("Application Exception: [{}] {}", ex.getErrorCode().getCode(), ex.getMessage());

        Map<String, Object> errorMetadata = new HashMap<>();
        errorMetadata.put("errorCode", ex.getErrorCode().getCode());
        if (ex.getDetails() != null) {
            errorMetadata.put("details", ex.getDetails());
        }

        return ResponseEntity.status(ex.getErrorCode().getHttpStatus())
                .body(ApiResponse.error(ex.getErrorCode().getHttpStatus(), ex.getMessage(), errorMetadata));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        log.warn("Validation failed: {}", errors);

        Map<String, Object> errorMetadata = new HashMap<>();
        errorMetadata.put("errorCode", AppErrorCode.VALIDATION_FAILED.getCode());
        errorMetadata.put("fieldErrors", errors);

        return ResponseEntity.status(AppErrorCode.VALIDATION_FAILED.getHttpStatus())
                .body(ApiResponse.error(AppErrorCode.VALIDATION_FAILED.getHttpStatus(), "Validation failed for one or more fields.", errorMetadata));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(AuthenticationException ex) {
        return handleAppException(new AppException(AppErrorCode.UNAUTHORIZED, ex.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException ex) {
        return handleAppException(new AppException(AppErrorCode.ACCESS_DENIED, ex.getMessage()));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        return handleAppException(new AppException(AppErrorCode.RESOURCE_NOT_FOUND, "The requested endpoint does not exist."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleAllOtherExceptions(Exception ex) {
        log.error("Unhandled exception caught:", ex);

        Map<String, Object> errorMetadata = new HashMap<>();
        errorMetadata.put("errorCode", AppErrorCode.INTERNAL_SERVER_ERROR.getCode());
        errorMetadata.put("type", ex.getClass().getSimpleName());

        return ResponseEntity.status(AppErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus())
                .body(ApiResponse.error(AppErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus(), AppErrorCode.INTERNAL_SERVER_ERROR.getDefaultMessage(), errorMetadata));
    }
}