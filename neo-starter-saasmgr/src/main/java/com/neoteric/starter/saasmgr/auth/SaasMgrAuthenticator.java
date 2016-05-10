package com.neoteric.starter.saasmgr.auth;

import com.neoteric.starter.saasmgr.principal.SaasMgrPrincipal;

public interface SaasMgrAuthenticator {
    SaasMgrPrincipal authenticate(String token, String customerId);
}
