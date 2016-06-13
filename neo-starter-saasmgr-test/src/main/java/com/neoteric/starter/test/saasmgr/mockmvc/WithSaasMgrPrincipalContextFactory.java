package com.neoteric.starter.test.saasmgr.mockmvc;

import com.neoteric.starter.saasmgr.auth.SaasMgrAuthenticationToken;
import com.neoteric.starter.saasmgr.model.SubscriptionConstraint;
import com.neoteric.starter.saasmgr.principal.DefaultSaasMgrPrincipal;
import com.neoteric.starter.saasmgr.principal.SaasMgrPrincipal;
import com.neoteric.starter.test.saasmgr.AuthenticationTokenHelper;
import org.assertj.core.util.Lists;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.neoteric.starter.test.saasmgr.AuthenticationTokenHelper.anonymousToken;
import static com.neoteric.starter.test.saasmgr.AuthenticationTokenHelper.getConstraints;

public class WithSaasMgrPrincipalContextFactory implements
		WithSecurityContextFactory<WithSaasMgrPrincipal> {

	@Override
    public SecurityContext createSecurityContext(WithSaasMgrPrincipal annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication token;
        if (annotation.noAuth()) {
            token = anonymousToken();
        } else {
            SaasMgrPrincipal principal = DefaultSaasMgrPrincipal.builder()
                    .customerId(annotation.customerId())
                    .customerName(annotation.customerName())
                    .userId(annotation.userId())
                    .email(annotation.email())
                    .status(annotation.accountStatus())
                    .features(Lists.newArrayList(annotation.features()))
                    .constraints(getConstraints(annotation.constraints()))
                    .build();

            token = new SaasMgrAuthenticationToken(principal, "token", principal.getAuthorities());
        }

        context.setAuthentication(token);
        return context;
    }
}