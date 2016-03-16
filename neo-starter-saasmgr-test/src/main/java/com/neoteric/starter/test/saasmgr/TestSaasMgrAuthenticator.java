package com.neoteric.starter.test.saasmgr;

import com.neoteric.starter.saasmgr.auth.SaasMgrAuthenticator;
import com.neoteric.starter.saasmgr.auth.DefaultSaasMgrPrincipal;

public class TestSaasMgrAuthenticator implements SaasMgrAuthenticator {

    private final DefaultSaasMgrPrincipal principal;

    public TestSaasMgrAuthenticator(DefaultSaasMgrPrincipal principal) {
        this.principal = principal;
    }

    @Override
    public DefaultSaasMgrPrincipal authenticate(String token, String customerId) {
        return principal;
    }
}