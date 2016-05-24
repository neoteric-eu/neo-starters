package com.neoteric.starter.exception;

public abstract class ResourceNotFoundException extends RuntimeException {

    private String errorCode;

    public ResourceNotFoundException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}