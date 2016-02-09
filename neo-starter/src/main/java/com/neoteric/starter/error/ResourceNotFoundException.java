package com.neoteric.starter.error;

public abstract class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String id, String resourceName) {
        super(resourceName + ": '" + id + "' not found.");
    }
}