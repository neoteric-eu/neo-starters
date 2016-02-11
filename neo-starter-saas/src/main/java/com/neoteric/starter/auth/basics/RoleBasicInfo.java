package com.neoteric.starter.auth.basics;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.util.Objects;


@JsonDeserialize(builder = RoleBasicInfo.Builder.class)
public class RoleBasicInfo {

    public static final String ROLE_ID = "roleId";
    public static final String ROLE_NAME = "roleName";

    @JsonProperty(ROLE_ID)
    private final String roleId;

    @JsonProperty(ROLE_NAME)
    private final String roleName;

    private final int cachedHashCode;

    public RoleBasicInfo(Builder builder) {
        this.roleId = builder.roleId;
        this.roleName = builder.roleName;

        this.cachedHashCode = calculateHashCode();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getRoleId() {
        return roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    @JsonPOJOBuilder(withPrefix = "set")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Builder {

        @JsonProperty(ROLE_ID)
        private String roleId;

        @JsonProperty(ROLE_NAME)
        private String roleName;

        public Builder setRoleId(String roleId) {
            this.roleId = roleId;
            return this;
        }

        public Builder setRoleName(String roleName) {
            this.roleName = roleName;
            return this;
        }

        public Builder copy(RoleBasicInfo other) {
            return this
                    .setRoleId(other.roleId)
                    .setRoleName(other.roleName);
        }

        public RoleBasicInfo build() {
            return new RoleBasicInfo(this);
        }
    }

    @Override
    public final int hashCode() {
        return this.cachedHashCode;
    }

    private int calculateHashCode() {
        return Objects.hash(roleId, roleName);
    }

    @Override
    @SuppressWarnings({"squid:MethodCyclomaticComplexity", "squid:S1067"})
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RoleBasicInfo)) {
            return false;
        }
        RoleBasicInfo other = (RoleBasicInfo) obj;
        return Objects.equals(this.roleId, other.roleId)
                && Objects.equals(this.roleName, other.roleName);
    }

}
