package com.rasa.Rasa_be.modules.shared.exception;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {
    private final AppErrorCode errorCode;
    private final Object details;

    public AppException(AppErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
        this.details = null;
    }

    public AppException(AppErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
        this.details = null;
    }

    public AppException(AppErrorCode errorCode, String customMessage, Object details) {
        super(customMessage);
        this.errorCode = errorCode;
        this.details = details;
    }
}