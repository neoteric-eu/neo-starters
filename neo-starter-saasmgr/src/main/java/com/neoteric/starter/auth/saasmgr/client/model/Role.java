package com.neoteric.starter.auth.saasmgr.client.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Role {

    public static final String ROLE_ID = "roleId";
    public static final String ROLE_NAME = "roleName";

    @JsonProperty(ROLE_ID)
    private final String roleId;

    @JsonProperty(ROLE_NAME)
    private final String roleName;

    private final int cachedHashCode;

    @JsonCreator
    public Role(@JsonProperty(ROLE_ID) String roleId, @JsonProperty(ROLE_NAME) String roleName) {
        this.roleId = roleId;
        this.roleName = roleName;

        this.cachedHashCode = calculateHashCode();
    }

    public String getRoleId() {
        return roleId;
    }

    public String getRoleName() {
        return roleName;
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
        if (!(obj instanceof Role)) {
            return false;
        }
        Role other = (Role) obj;
        return Objects.equals(this.roleId, other.roleId)
                && Objects.equals(this.roleName, other.roleName);
    }

}
