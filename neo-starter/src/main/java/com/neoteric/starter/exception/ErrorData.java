package com.neoteric.starter.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.MoreObjects;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorData {

    private final ZonedDateTime timestamp;
    private final String requestId;
    private final Integer status;
    private final String error;
    private final Object errorCode;
    private final String path;
    private final Object message;
    private final String exception;
    private final Map<String, String> stackTrace;

    private final int cachedHashCode;

    public ErrorData(Builder builder) {
        this.timestamp = builder.timestamp;
        this.path = builder.path;
        this.status = builder.status;
        this.error = builder.error;
        this.errorCode = builder.errorCode;
        this.exception = builder.exception;
        this.stackTrace = builder.stackTrace;
        this.message = builder.message;
        this.requestId = builder.requestId;
        this.cachedHashCode = calculateHashCode();
    }

    public static Builder builder() {
        return new Builder();
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public String getError() {
        return error;
    }

    public Object getErrorCode() {
        return errorCode;
    }

    public String getException() {
        return exception;
    }

    public Map<String,String> getStackTrace() {
        return stackTrace;
    }

    public String getPath() {
        return path;
    }

    public Integer getStatus() {
        return status;
    }

    public Object getMessage() {
        return message;
    }

    public String getRequestId() {
        return requestId;
    }

    public static class Builder {

        private ZonedDateTime timestamp;
        private String path;
        private Integer status;
        private String error;
        private Object errorCode;
        private String exception;
        private Map<String, String> stackTrace;
        private Object message;
        private String requestId;

        public Builder setTimestamp(ZonedDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder setPath(String path) {
            this.path = path;
            return this;
        }

        public Builder setStatus(Integer status) {
            this.status = status;
            return this;
        }

        public Builder setError(String error) {
            this.error = error;
            return this;
        }

        public Builder setErrorCode(Object errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public Builder setException(String exception) {
            this.exception = exception;
            return this;
        }

        public Builder setStackTrace(Map<String, String> stackTrace) {
            this.stackTrace = stackTrace;
            return this;
        }

        public Builder setMessage(Object message) {
            this.message = message;
            return this;
        }

        public Builder setRequestId(String requestId) {
            this.requestId = requestId;
            return this;
        }

        public Builder copy(ErrorData other) {
            return this
                    .setTimestamp(other.timestamp)
                    .setPath(other.path)
                    .setStatus(other.status)
                    .setError(other.error)
                    .setErrorCode(other.errorCode)
                    .setException(other.exception)
                    .setStackTrace(other.stackTrace)
                    .setMessage(other.message)
                    .setRequestId(other.requestId);
        }

        public ErrorData build() {
            return new ErrorData(this);
        }
    }

    @Override
    public final int hashCode() {
        return this.cachedHashCode;
    }

    private int calculateHashCode() {
        return Objects.hash(timestamp, path, status, message, error, errorCode, exception, stackTrace, requestId);
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof ErrorData)) {
            return false;
        }
        ErrorData other = (ErrorData) obj;
        return Objects.equals(this.timestamp, other.timestamp)
                && Objects.equals(this.path, other.path)
                && Objects.equals(this.status, other.status)
                && Objects.equals(this.error, other.error)
                && Objects.equals(this.errorCode, other.errorCode)
                && Objects.equals(this.exception, other.exception)
                && Objects.equals(this.stackTrace, other.stackTrace)
                && Objects.equals(this.message, other.message)
                && Objects.equals(this.requestId, other.requestId);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("timestamp", timestamp)
                .add("path", path)
                .add("status", status)
                .add("error", error)
                .add("errorCode", errorCode)
                .add("exception", exception)
                .add("stackTrace", stackTrace)
                .add("message", message)
                .add("requestId", requestId)
                .add("cachedHashCode", cachedHashCode)
                .toString();
    }
}