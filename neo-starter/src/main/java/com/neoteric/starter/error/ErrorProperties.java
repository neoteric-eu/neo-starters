package com.neoteric.starter.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.MoreObjects;

import java.time.ZonedDateTime;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorProperties {

    private final ZonedDateTime timestamp;
    private final Integer status;
    private final String error;
    private final String path;
    private final String exception;
    private final String message;
    private final String requestId;

    private final int cachedHashCode;

    public ErrorProperties(Builder builder) {
        this.timestamp = builder.timestamp;
        this.path = builder.path;
        this.status = builder.status;
        this.error = builder.error;
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

    public String getPath() {
        return path;
    }

    public Integer getStatus() {
        return status;
    }

    public String getMessage() {
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
        private String message;
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

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setRequestId(String requestId) {
            this.requestId = requestId;
            return this;
        }

        public Builder copy(ErrorProperties other) {
            return this
                    .setTimestamp(other.timestamp)
                    .setPath(other.path)
                    .setStatus(other.status)
                    .setError(other.error)
                    .setMessage(other.message)
                    .setRequestId(other.requestId);
        }

        public ErrorProperties build() {
            return new ErrorProperties(this);
        }
    }

    @Override
    public final int hashCode() {
        return this.cachedHashCode;
    }

    private int calculateHashCode() {
        return Objects.hash(timestamp, path, status, message, requestId);
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof ErrorProperties)) {
            return false;
        }
        ErrorProperties other = (ErrorProperties) obj;
        return Objects.equals(this.timestamp, other.timestamp)
                && Objects.equals(this.path, other.path)
                && Objects.equals(this.status, other.status)
                && Objects.equals(this.error, other.error)
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
                .add("message", message)
                .add("requestId", requestId)
                .add("cachedHashCode", cachedHashCode)
                .toString();
    }
}