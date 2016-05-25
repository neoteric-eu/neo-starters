package com.neoteric.starter.exception;

public class ResourceConflictException extends RuntimeException {

    private String errorCode;

    public ResourceConflictException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
    }

    public ResourceConflictException(String errorMessage) {
        super(errorMessage);
    }

    public String getErrorCode() {
        return errorCode;
    }
}
