package com.neoteric.starter.auth.saasmgr.test;

import com.neoteric.starter.auth.saasmgr.SaasMgrAuthenticationToken;
import com.neoteric.starter.auth.saasmgr.SaasMgrPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Arrays;

public class WithSaasMgrSecurityFactory implements WithSecurityContextFactory<WithSaasMgrAuthentication> {

    @Override
    public SecurityContext createSecurityContext(WithSaasMgrAuthentication annotation) {

        SaasMgrPrincipal saasDetails = new SaasMgrPrincipal.Builder()
                .customerId(annotation.customerId())
                .customerName(annotation.customerName())
                .email(annotation.email())
                .features(Arrays.asList(annotation.features()))
                .build();

        SaasMgrAuthenticationToken authentication = new SaasMgrAuthenticationToken(saasDetails, null);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }
}
