package com.neoteric.starter.saasmgr;

import com.neoteric.starter.saasmgr.auth.SaasMgrAuthenticationToken;
import com.neoteric.starter.saasmgr.principal.Feature;
import com.neoteric.starter.saasmgr.principal.SaasMgrPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

@Slf4j
public final class SaasMgrAuthUtils {

    private SaasMgrAuthUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static SaasMgrPrincipal getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SaasMgrAuthenticationToken saasToken = (SaasMgrAuthenticationToken)authentication;
        return (SaasMgrPrincipal)saasToken.getPrincipal();
    }

    public static boolean isNotAuthenticated() {
        return !isAuthenticated();
    }

    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof SaasMgrAuthenticationToken)) {
            return false;
        }
        SaasMgrAuthenticationToken saasToken = (SaasMgrAuthenticationToken)authentication;
        return saasToken.isAuthenticated();
    }

    @SuppressWarnings("unchecked")
    public static boolean isAuthorizedForFeature(String feature) {
        SaasMgrPrincipal principal = getPrincipal();
        Collection<Feature> authorities = (Collection<Feature>) principal.getAuthorities();
        boolean authorisedForFeature = authorities.stream().
                map(Feature::getAuthority)
                .filter(f -> f.equals(feature))
                .findAny().isPresent();
        LOG.debug("User [userId:{}, customerId:{}] authorised for feature: {}",
                principal.getUserId(), principal.getCustomerId(), feature);
        return authorisedForFeature;
    }
}