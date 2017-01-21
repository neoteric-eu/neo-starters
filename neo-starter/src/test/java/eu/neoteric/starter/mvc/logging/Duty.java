package eu.neoteric.starter.mvc.logging;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.ToString;

import java.util.Objects;


@JsonDeserialize(builder = Duty.Builder.class)
@ToString
public class Duty {

    public static final String NAME = "name";
    public static final String VALUE = "value";

    @JsonProperty(NAME)
    private final String name;

    @JsonProperty(VALUE)
    private final Integer value;

    private final int cachedHashCode;

    public Duty(String name, Integer value) {
        this.name = name;
        this.value = value == null ? 0 : value;

        this.cachedHashCode = calculateHashCode();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public Integer getValue() {
        return value;
    }

    @JsonPOJOBuilder(withPrefix = "set")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Builder {

        @JsonProperty(NAME)
        private String name;

        @JsonProperty(VALUE)
        private Integer value;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setValue(Integer value) {
            this.value = value;
            return this;
        }

        public Builder copy(Duty other) {
            return this
                    .setName(other.name)
                    .setValue(other.value);
        }

        public Duty build() {
            return new Duty(name, value);
        }
    }

    @Override
    public final int hashCode() {
        return this.cachedHashCode;
    }

    private int calculateHashCode() {
        return Objects.hash(name, value);
    }

    @Override
    @SuppressWarnings({"squid:MethodCyclomaticComplexity", "squid:S1067"})
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Duty)) {
            return false;
        }
        Duty other = (Duty) obj;
        return Objects.equals(this.name, other.name)
                && Objects.equals(this.value, other.value);
    }

}