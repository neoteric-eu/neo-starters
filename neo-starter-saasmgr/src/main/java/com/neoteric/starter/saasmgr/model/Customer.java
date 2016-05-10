package com.neoteric.starter.saasmgr.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder(toBuilder = true)
@AllArgsConstructor
public class Customer {

    private final String customerId;
    private final String customerName;
    private final List<Role> roles;
    private final List<String> featureKeys;
    private final List<SubscriptionConstraint> constraints;
    private final AccountStatus status;
}
