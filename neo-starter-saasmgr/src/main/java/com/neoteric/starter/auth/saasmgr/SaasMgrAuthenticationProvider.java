package com.neoteric.starter.auth.saasmgr;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class SaasMgrAuthenticationProvider implements AuthenticationProvider {

    private final SaasMgrAuthenticator saasMgrAuthenticator;

    public SaasMgrAuthenticationProvider(SaasMgrAuthenticator saasMgrAuthenticator) {
        this.saasMgrAuthenticator = saasMgrAuthenticator;
    }
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        SaasMgrAuthenticationToken saasMgrToken = (SaasMgrAuthenticationToken)authentication;
        SaasMgrPrincipal saasMgrPrincipal =
                saasMgrAuthenticator.authenticate(String.valueOf(saasMgrToken.getCredentials()),
                String.valueOf(saasMgrToken.getPrincipal()));

        return new SaasMgrAuthenticationToken(saasMgrPrincipal, saasMgrToken.getCredentials(), saasMgrPrincipal.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return SaasMgrAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
