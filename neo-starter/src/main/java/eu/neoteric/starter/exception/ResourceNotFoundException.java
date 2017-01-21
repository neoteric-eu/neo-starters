package eu.neoteric.starter.exception;

public abstract class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String id, String resourceName) {
        super(resourceName + " '" + id + "' not found");
    }
    
    public ResourceNotFoundException(String fieldName, String value, String resourceName) {
        super(resourceName + " with " + fieldName + " = " + value + " not found");
    }
}