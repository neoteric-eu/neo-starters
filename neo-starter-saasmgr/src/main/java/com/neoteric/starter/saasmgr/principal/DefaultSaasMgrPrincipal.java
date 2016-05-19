package com.neoteric.starter.saasmgr.principal;

import com.neoteric.starter.saasmgr.model.AccountStatus;
import com.neoteric.starter.saasmgr.model.SubscriptionConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class DefaultSaasMgrPrincipal implements SaasMgrPrincipal {

    private final String userId;
    private final String email;
    private final String customerId;
    private final String customerName;
    private final List<SubscriptionConstraint> constraints;
    @Getter(AccessLevel.NONE)
    private final List<Feature> features;
    private final AccountStatus status;

    public DefaultSaasMgrPrincipal(String userId, String email, String customerId, String customerName,
                                   List<String> features, List<SubscriptionConstraint> constraints, AccountStatus status) {

        this.userId = userId;
        this.email = email;
        this.customerId = customerId;
        this.customerName = customerName;
        this.constraints = constraints;
        this.features = features.stream().map(Feature::of).collect(Collectors.toList());
        this.status = status;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return features;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String userId;
        private String email;
        private String customerId;
        private String customerName;
        private List<SubscriptionConstraint> constraints;
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

        public Builder constraints(List<SubscriptionConstraint> constraints) {
            this.constraints = constraints;
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
            return new DefaultSaasMgrPrincipal(userId, email, customerId, customerName, features, constraints, status);
        }

    }
}
