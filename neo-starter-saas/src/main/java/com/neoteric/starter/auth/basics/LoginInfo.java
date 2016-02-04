package com.neoteric.starter.auth.basics;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.util.Objects;


@JsonDeserialize(builder = LoginInfo.Builder.class)
public class LoginInfo {

    public static final String TOKEN = "token";
    public static final String USER = "user";

    @JsonProperty(TOKEN)
    private final String token;

    @JsonProperty(USER)
    private final UserSaasInfo user;

    private final int cachedHashCode;

    public LoginInfo(Builder builder) {
        this.token = builder.token;
        this.user = builder.user;

        this.cachedHashCode = calculateHashCode();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getToken() {
        return token;
    }

    public UserSaasInfo getUser() {
        return user;
    }

    @JsonPOJOBuilder(withPrefix = "set")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Builder {

        @JsonProperty(TOKEN)
        private String token;

        @JsonProperty(USER)
        private UserSaasInfo user;

        public Builder setToken(String token) {
            this.token = token;
            return this;
        }

        public Builder setUser(UserSaasInfo user) {
            this.user = user;
            return this;
        }

        public Builder copy(LoginInfo other) {
            return this
                    .setToken(other.token)
                    .setUser(other.user);
        }

        public LoginInfo build() {
            return new LoginInfo(this);
        }
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
        if (!(obj instanceof LoginInfo)) {
            return false;
        }
        LoginInfo other = (LoginInfo) obj;
        return Objects.equals(this.token, other.token)
                && Objects.equals(this.user, other.user);
    }

}
