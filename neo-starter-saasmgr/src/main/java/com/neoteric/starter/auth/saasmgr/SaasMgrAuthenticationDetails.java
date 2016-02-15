package com.neoteric.starter.auth.saasmgr;

import com.neoteric.starter.auth.saasmgr.client.model.AccountStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class SaasMgrAuthenticationDetails implements UserDetails {

    private final String userId;
    private final String email;
    private final String customerId;
    private final String customerName;
    private final List<Feature> features;
    private final AccountStatus status;

    public SaasMgrAuthenticationDetails(String userId, String email, String customerId, String customerName, List<String> features, AccountStatus status) {

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

    public String getUserId() {
        return userId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getEmail() {
        return email;
    }

    public AccountStatus getStatus() {
        return status;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return userId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Feature implements GrantedAuthority {

        private final String name;

        private Feature(String name) {
            this.name = name;
        }

        @Override
        public String getAuthority() {
            return name;
        }

        public static Feature of(String name) {
            return new Feature(name);
        }
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

        public SaasMgrAuthenticationDetails build() {
            return new SaasMgrAuthenticationDetails(userId, email, customerId, customerName, features, status);
        }


    }
}
