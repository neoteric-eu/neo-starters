package com.neoteric.starter.saasmgr.auth;

import com.google.common.base.MoreObjects;
import org.springframework.security.core.GrantedAuthority;

public class Feature implements GrantedAuthority {

    private final String name;

    private Feature(String name) {
        this.name = name;
    }

    @Override
    public String getAuthority() {
        return name;
    }

    public static Feature of(String name) {
        return new Feature(name);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", name)
                .toString();
    }
}