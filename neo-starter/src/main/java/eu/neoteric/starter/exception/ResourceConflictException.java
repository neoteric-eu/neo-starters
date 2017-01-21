package eu.neoteric.starter.exception;

public abstract class ResourceConflictException extends RuntimeException {

    public ResourceConflictException(String id, String resourceName) {
        super(resourceName + " '" + id + "' in conflict");
    }

    public ResourceConflictException(String fieldName, String value, String resourceName) {
        super(resourceName + " with " + fieldName + " = " + value + " in conflict");
    }
}
