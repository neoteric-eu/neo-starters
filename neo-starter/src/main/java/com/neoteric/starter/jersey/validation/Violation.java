package com.neoteric.starter.jersey.validation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import java.util.Objects;

public class Violation {

    @JsonProperty("property")
    private final String property;
    @JsonProperty("violationType")
    private final String violationType;
    @JsonProperty("invalidValue")
    private final Object invalidValue;
    @JsonProperty("message")
    private final String message;

    @JsonCreator
    public Violation(@JsonProperty("property") String property,
                     @JsonProperty("violationType") String violationType,
                     @JsonProperty("invalidValue") Object invalidValue,
                     @JsonProperty("message") String message) {
        this.property = property;
        this.invalidValue = invalidValue;
        this.violationType = violationType;
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Violation violation = (Violation) o;
        return Objects.equals(property, violation.property) &&
                Objects.equals(invalidValue, violation.invalidValue) &&
                Objects.equals(violationType, violation.violationType) &&
                Objects.equals(message, violation.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(property, invalidValue, violationType, message);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("property", property)
                .add("invalidValue", invalidValue)
                .add("violationType", violationType)
                .add("message", message)
                .toString();
    }
}
