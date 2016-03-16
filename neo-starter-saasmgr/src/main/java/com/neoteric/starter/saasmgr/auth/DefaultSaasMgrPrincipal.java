package com.neoteric.starter.saasmgr.auth;

import com.google.common.base.MoreObjects;
import com.neoteric.starter.saasmgr.client.model.AccountStatus;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultSaasMgrPrincipal implements SaasMgrPrincipal {

    private final String userId;
    private final String email;
    private final String customerId;
    private final String customerName;
    private final List<Feature> features;
    private final AccountStatus status;

    public DefaultSaasMgrPrincipal(String userId, String email, String customerId, String customerName,
                                   List<String> features, AccountStatus status) {

        this.userId = userId;
        this.email = email;
        this.customerId = customerId;
        this.customerName = customerName;
        this.features = features.stream().map(Feature::of).collect(Collectors.toList());
        this.status = status;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return features;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public String getCustomerId() {
        return customerId;
    }

    @Override
    public String getCustomerName() {
        return customerName;
    }

    @Override
    public String getEmail() {
        return email;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String userId;
        private String email;
        private String customerId;
        private String customerName;
        private List<String> features;
        private AccountStatus status;

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder customerId(String customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder features(List<String> features) {
            this.features = features;
            return this;
        }

        public Builder customerName(String customerName) {
            this.customerName = customerName;
            return this;
        }

        public Builder status(AccountStatus status) {
            this.status = status;
            return this;
        }

        public DefaultSaasMgrPrincipal build() {
            return new DefaultSaasMgrPrincipal(userId, email, customerId, customerName, features, status);
        }

    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("userId", userId)
                .add("email", email)
                .add("customerId", customerId)
                .add("customerName", customerName)
                .add("features", features)
                .add("status", status)
                .toString();
    }
}
