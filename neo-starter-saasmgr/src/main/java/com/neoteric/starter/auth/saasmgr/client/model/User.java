package com.neoteric.starter.auth.saasmgr.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.util.List;
import java.util.Objects;

@JsonDeserialize(builder = User.Builder.class)
public class User {

    public static final String ID = "id";
    public static final String EMAIL = "email";
    public static final String CUSTOMERS = "customers";

    @JsonProperty(ID)
    private final String id;

    @JsonProperty(EMAIL)
    private final String email;

    @JsonProperty(CUSTOMERS)
    private final List<Customer> customers;

    private final int cachedHashCode;

    public User(Builder builder) {
        this.id = builder.id;
        this.email = builder.email;
        this.customers = builder.customers;

        this.cachedHashCode = calculateHashCode();
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    @JsonPOJOBuilder(withPrefix = "set")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Builder {

        @JsonProperty(ID)
        private String id;

        @JsonProperty(EMAIL)
        private String email;

        @JsonProperty(CUSTOMERS)
        private List<Customer> customers;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setCustomers(List<Customer> customers) {
            this.customers = customers;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }

    @Override
    public final int hashCode() {
        return this.cachedHashCode;
    }

    private int calculateHashCode() {
        return Objects.hash(id, email, customers);
    }

    @Override
    @SuppressWarnings({"squid:MethodCyclomaticComplexity", "squid:S1067"})
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof User)) {
            return false;
        }
        User other = (User) obj;
        return Objects.equals(this.id, other.id)
                && Objects.equals(this.email, other.email)
                && Objects.equals(this.customers, other.customers);
    }
}
