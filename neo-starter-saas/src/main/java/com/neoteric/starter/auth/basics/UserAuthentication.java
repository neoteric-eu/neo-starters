package com.neoteric.starter.auth.basics;

import java.util.List;

public class UserAuthentication {
    private String userId;
    private String customerId;
    private String username;
    private List<String> features;

    public UserAuthentication(String userId, String username, String customerId, List<String> features) {
        this.userId = userId;
        this.username = username;
        this.customerId = customerId;
        this.features = features;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getUserId() {
        return userId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }
}
