package com.neoteric.starter.saasmgr;

import com.neoteric.starter.saasmgr.auth.SaasMgrAuthenticationToken;
import com.neoteric.starter.saasmgr.principal.SaasMgrPrincipal;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SaasMgrAuthUtils {

    private SaasMgrAuthUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static SaasMgrPrincipal getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SaasMgrAuthenticationToken saasToken = (SaasMgrAuthenticationToken)authentication;
        return (SaasMgrPrincipal)saasToken.getPrincipal();
    }

    public static boolean isAnonymous() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication instanceof AnonymousAuthenticationToken;
    }

    public static boolean isAuthorized() {
        return !isAnonymous();
    }

}