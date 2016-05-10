package com.neoteric.starter.saasmgr.principal;

import com.neoteric.starter.saasmgr.model.AccountStatus;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public interface SaasMgrPrincipal {
    String getUserId();
    String getCustomerId();
    String getCustomerName();
    String getEmail();
    Collection<? extends GrantedAuthority> getAuthorities();
    AccountStatus getStatus();
}
