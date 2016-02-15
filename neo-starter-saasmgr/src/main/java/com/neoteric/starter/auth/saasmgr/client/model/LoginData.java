package com.neoteric.starter.auth.saasmgr.client.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class LoginData {

    public static final String TOKEN = "token";
    public static final String USER = "user";

    @JsonProperty(TOKEN)
    private final String token;

    @JsonProperty(USER)
    private final User user;

    private final int cachedHashCode;

    @JsonCreator
    public LoginData(@JsonProperty(TOKEN) String token, @JsonProperty(USER) User user) {
        this.token = token;
        this.user = user;
        this.cachedHashCode = calculateHashCode();
    }

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }

    @Override
    public final int hashCode() {
        return this.cachedHashCode;
    }

    private int calculateHashCode() {
        return Objects.hash(token, user);
    }

    @Override
    @SuppressWarnings({"squid:MethodCyclomaticComplexity", "squid:S1067"})
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof LoginData)) {
            return false;
        }
        LoginData other = (LoginData) obj;
        return Objects.equals(this.token, other.token)
                && Objects.equals(this.user, other.user);
    }

}
