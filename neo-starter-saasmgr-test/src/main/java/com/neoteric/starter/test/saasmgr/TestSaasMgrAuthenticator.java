package com.neoteric.starter.test.saasmgr;

import com.neoteric.starter.saasmgr.auth.SaasMgrAuthenticator;
import com.neoteric.starter.saasmgr.auth.SaasMgrPrincipal;

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