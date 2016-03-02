package com.neoteric.starter.auth.saasmgr.test;

import com.neoteric.starter.auth.saasmgr.SaasMgrAuthenticator;
import com.neoteric.starter.auth.saasmgr.SaasMgrPrincipal;
import org.springframework.security.test.context.TestSecurityContextHolder;

public class TestSaasMgrAuthenticator implements SaasMgrAuthenticator {

    @Override
    public SaasMgrPrincipal authenticate(String token, String customerId) {
        return (SaasMgrPrincipal) TestSecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
