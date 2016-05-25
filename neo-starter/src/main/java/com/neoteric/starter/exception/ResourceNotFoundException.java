package com.neoteric.starter.exception;

public abstract class ResourceNotFoundException extends RuntimeException {

    private String errorCode;

    public ResourceNotFoundException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
    }

    public ResourceNotFoundException(String errorMessage) {
        super(errorMessage);
    }

    public String getErrorCode() {
        return errorCode;
    }
}