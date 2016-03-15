package com.neoteric.starter.saasmgr.auth;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public interface SaasMgrPrincipal {
    String getUserId();
    String getCustomerId();
    String getCustomerName();
    String getEmail();
    Collection<? extends GrantedAuthority> getAuthorities();
}
