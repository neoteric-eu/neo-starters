package com.neoteric.starter.auth.basics;

import java.util.List;

public class UserAuthentication {
    private String userId;
    private String customerId;
    private String email;
    private List<String> features;

    public UserAuthentication(String userId, String email, String customerId, List<String> features) {
        this.userId = userId;
        this.email = email;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }
}
