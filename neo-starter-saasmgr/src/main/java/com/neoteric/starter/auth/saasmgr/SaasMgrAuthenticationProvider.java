package com.neoteric.starter.auth.saasmgr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class SaasMgrAuthenticationProvider implements AuthenticationProvider {

    private static final Logger LOG = LoggerFactory.getLogger(SaasMgrAuthenticationProvider.class);
    private final SaasMgrConnector saasMgrConnector;

    public SaasMgrAuthenticationProvider(SaasMgrConnector saasMgrConnector) {
        this.saasMgrConnector = saasMgrConnector;
    }
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        SaasMgrAuthenticationToken saasMgrToken = (SaasMgrAuthenticationToken)authentication;
        SaasMgrAuthenticationDetails saasMgrAuthenticationDetails =
                saasMgrConnector.getSaasMgrAuthenticationDetails(String.valueOf(saasMgrToken.getCredentials()),
                String.valueOf(saasMgrToken.getPrincipal()));

        return new SaasMgrAuthenticationToken(saasMgrAuthenticationDetails, saasMgrToken.getCredentials(), saasMgrAuthenticationDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return SaasMgrAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
