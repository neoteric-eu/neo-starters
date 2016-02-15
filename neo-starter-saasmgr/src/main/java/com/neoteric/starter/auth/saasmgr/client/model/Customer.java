package com.neoteric.starter.auth.saasmgr.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.util.List;
import java.util.Objects;

@JsonDeserialize(builder = Customer.Builder.class)
public class Customer {

    public static final String CUSTOMER_ID = "customerId";
    public static final String CUSTOMER_NAME = "customerName";
    public static final String ROLES = "roles";
    public static final String FEATURE_KEYS = "featureKeys";
    public static final String STATUS = "status";

    @JsonProperty(CUSTOMER_ID)
    private final String customerId;

    @JsonProperty(CUSTOMER_NAME)
    private final String customerName;

    @JsonProperty(ROLES)
    private final List<Role> roles;

    @JsonProperty(FEATURE_KEYS)
    private final List<String> featureKeys;

    @JsonProperty(STATUS)
    private final AccountStatus status;

    private final int cachedHashCode;

    public Customer(Builder builder) {
        this.customerId = builder.customerId;
        this.customerName = builder.customerName;
        this.roles = builder.roles;
        this.featureKeys = builder.featureKeys;
        this.status = builder.status;

        this.cachedHashCode = calculateHashCode();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public List<String> getFeatureKeys() {
        return featureKeys;
    }

    public AccountStatus getStatus() {
        return status;
    }

    @JsonPOJOBuilder(withPrefix = "set")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Builder {

        @JsonProperty(CUSTOMER_ID)
        private String customerId;

        @JsonProperty(CUSTOMER_NAME)
        private String customerName;

        @JsonProperty(ROLES)
        private List<Role> roles;

        @JsonProperty(FEATURE_KEYS)
        private List<String> featureKeys;

        @JsonProperty(STATUS)
        private AccountStatus status;

        public Builder setCustomerId(String customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder setCustomerName(String customerName) {
            this.customerName = customerName;
            return this;
        }

        public Builder setRoles(List<Role> roles) {
            this.roles = roles;
            return this;
        }

        public Builder setFeatureKeys(List<String> featureKeys) {
            this.featureKeys = featureKeys;
            return this;
        }

        public Builder setStatus(AccountStatus status) {
            this.status = status;
            return this;
        }

        public Customer build() {
            return new Customer(this);
        }
    }

    @Override
    public final int hashCode() {
        return this.cachedHashCode;
    }

    private int calculateHashCode() {
        return Objects.hash(customerId, customerName, roles, featureKeys, status);
    }

    @Override
    @SuppressWarnings({"squid:MethodCyclomaticComplexity", "squid:S1067"})
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Customer)) {
            return false;
        }
        Customer other = (Customer) obj;
        return Objects.equals(this.customerId, other.customerId)
                && Objects.equals(this.customerName, other.customerName)
                && Objects.equals(this.roles, other.roles)
                && Objects.equals(this.featureKeys, other.featureKeys)
                && Objects.equals(this.status, other.status);
    }

}
