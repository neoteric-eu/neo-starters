package com.neoteric.starter.auth.saasmgr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class SaasMgrAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    SaasMgrConnector saasDetailsFetcher;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        SaasMgrAuthenticationToken saasMgrToken = (SaasMgrAuthenticationToken)authentication;
        SaasMgrAuthenticationDetails saasMgrAuthenticationDetails =
                saasDetailsFetcher.getSaasMgrAuthenticationDetails(String.valueOf(saasMgrToken.getCredentials()),
                String.valueOf(saasMgrToken.getPrincipal()));

        return new SaasMgrAuthenticationToken(saasMgrAuthenticationDetails, saasMgrToken.getCredentials(), saasMgrAuthenticationDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return SaasMgrAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
