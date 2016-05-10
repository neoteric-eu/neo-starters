package com.neoteric.starter.saasmgr.principal;

import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

@ToString
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
}