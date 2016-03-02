package com.neoteric.starter.auth.saasmgr.test;

import com.neoteric.starter.auth.saasmgr.SaasMgrAuthenticator;
import com.neoteric.starter.auth.saasmgr.SaasMgrPrincipal;

public class TestSaasMgrAuthenticator implements SaasMgrAuthenticator {

    private final SaasMgrPrincipal principal;

    public TestSaasMgrAuthenticator(SaasMgrPrincipal principal) {
        this.principal = principal;
    }

    @Override
    public SaasMgrPrincipal authenticate(String token, String customerId) {
        return principal;
    }
}