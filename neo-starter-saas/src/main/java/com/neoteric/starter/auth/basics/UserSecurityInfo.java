package com.neoteric.starter.auth.basics;

import java.util.List;

public class UserSecurityInfo {
    private String username;
    private List<String> features;

    public UserSecurityInfo(String username, List<String> features) {
        this.username = username;
        this.features = features;
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
