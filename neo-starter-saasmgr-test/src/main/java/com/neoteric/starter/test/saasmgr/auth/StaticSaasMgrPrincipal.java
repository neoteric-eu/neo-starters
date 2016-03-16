package com.neoteric.starter.test.saasmgr.auth;

import com.neoteric.starter.saasmgr.auth.Feature;
import com.neoteric.starter.saasmgr.auth.SaasMgrPrincipal;
import com.neoteric.starter.saasmgr.client.model.AccountStatus;
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public final class StaticSaasMgrPrincipal implements SaasMgrPrincipal {

    public static String userId;
    public static String customerId;
    public static String customerName;
    public static String email;
    public static AccountStatus accountStatus;
    public static String[] features;

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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.stream(features).map(Feature::of).collect(Collectors.toList());
    }

    @Override
    public AccountStatus getStatus() {
        return accountStatus;
    }
}
