package com.neoteric.starter.auth.basics;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.util.Objects;


@JsonDeserialize(builder = SubscriptionConstraintInfo.Builder.class)
public class SubscriptionConstraintInfo {

    public static final String KEY = "key";
    public static final String MAX_VALUE = "maxValue";
    public static final String CURRENT_VALUE = "currentValue";

    @JsonProperty(KEY)
    private final String key;

    @JsonProperty(MAX_VALUE)
    private final Double maxValue;

    @JsonProperty(CURRENT_VALUE)
    private final Double currentValue;

    private final int cachedHashCode;

    public SubscriptionConstraintInfo(Builder builder) {
        this.key = builder.key;
        this.maxValue = builder.maxValue;
        this.currentValue = builder.currentValue;

        this.cachedHashCode = calculateHashCode();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getKey() {
        return key;
    }

    public Double getMaxValue() {
        return maxValue;
    }

    public Double getCurrentValue() {
        return currentValue;
    }

    @JsonPOJOBuilder(withPrefix = "set")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Builder {

        @JsonProperty(KEY)
        private String key;

        @JsonProperty(MAX_VALUE)
        private Double maxValue;

        @JsonProperty(CURRENT_VALUE)
        private Double currentValue;

        public Builder setKey(String key) {
            this.key = key;
            return this;
        }

        public Builder setMaxValue(Double maxValue) {
            this.maxValue = maxValue;
            return this;
        }

        public Builder setCurrentValue(Double currentValue) {
            this.currentValue = currentValue;
            return this;
        }

        public Builder copy(SubscriptionConstraintInfo other) {
            return this
                    .setKey(other.key)
                    .setMaxValue(other.maxValue)
                    .setCurrentValue(other.currentValue);
        }

        public SubscriptionConstraintInfo build() {
            return new SubscriptionConstraintInfo(this);
        }
    }

    @Override
    public final int hashCode() {
        return this.cachedHashCode;
    }

    private int calculateHashCode() {
        return Objects.hash(key, maxValue, currentValue);
    }

    @Override
    @SuppressWarnings({"squid:MethodCyclomaticComplexity", "squid:S1067"})
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SubscriptionConstraintInfo)) {
            return false;
        }
        SubscriptionConstraintInfo other = (SubscriptionConstraintInfo) obj;
        return Objects.equals(this.key, other.key)
                && Objects.equals(this.maxValue, other.maxValue)
                && Objects.equals(this.currentValue, other.currentValue);
    }

}
