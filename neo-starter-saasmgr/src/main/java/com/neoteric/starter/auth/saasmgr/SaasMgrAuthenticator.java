package com.neoteric.starter.auth.saasmgr;

public interface SaasMgrAuthenticator {
    SaasMgrPrincipal authenticate(String token, String customerId);
}
