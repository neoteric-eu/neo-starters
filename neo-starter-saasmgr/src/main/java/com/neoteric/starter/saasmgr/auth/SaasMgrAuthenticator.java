package com.neoteric.starter.saasmgr.auth;

public interface SaasMgrAuthenticator {
    DefaultSaasMgrPrincipal authenticate(String token, String customerId);
}
