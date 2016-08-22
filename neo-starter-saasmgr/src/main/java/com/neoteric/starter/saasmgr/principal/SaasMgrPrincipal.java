package com.neoteric.starter.saasmgr.principal;

import com.neoteric.starter.saasmgr.model.AccountStatus;
import com.neoteric.starter.saasmgr.model.SubscriptionConstraint;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

public interface SaasMgrPrincipal {
    String getUserId();
    String getCustomerId();
    String getCustomerName();
    String getEmail();
    String getFirstName();
    String getLastName();
    Collection<? extends GrantedAuthority> getAuthorities();
    List<SubscriptionConstraint> getConstraints();
    AccountStatus getStatus();
}
