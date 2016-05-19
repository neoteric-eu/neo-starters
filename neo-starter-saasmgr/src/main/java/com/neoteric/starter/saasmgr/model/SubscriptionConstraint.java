package com.neoteric.starter.saasmgr.model;

import lombok.Value;

@Value
public class SubscriptionConstraint {
    private final String key;
    private final Double maxValue;
    private final Double currentValue;
}
