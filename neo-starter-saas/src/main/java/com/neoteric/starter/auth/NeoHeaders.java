package com.neoteric.starter.auth;

public enum NeoHeaders {

    AUTHORIZATION("Authorization"),
    X_CUSTOMER_ID("X-Customer-Id"),
    X_API_KEY("X-Api-Key"),
    X_API_SECRET("X-Api-Secret");

    private final String value;

    private NeoHeaders(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
