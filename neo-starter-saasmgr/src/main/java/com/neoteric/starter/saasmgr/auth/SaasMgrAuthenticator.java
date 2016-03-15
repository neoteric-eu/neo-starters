package com.neoteric.starter.saasmgr.auth;

public interface SaasMgrAuthenticator {
    SaasMgrPrincipal authenticate(String token, String customerId);
}
