package com.neoteric.starter.test.saasmgr.mockmvc;

import com.neoteric.starter.saasmgr.auth.SaasMgrAuthenticationToken;
import com.neoteric.starter.saasmgr.model.SubscriptionConstraint;
import com.neoteric.starter.saasmgr.principal.DefaultSaasMgrPrincipal;
import com.neoteric.starter.saasmgr.principal.SaasMgrPrincipal;
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

public class WithSaasMgrPrincipalContextFactory implements
		WithSecurityContextFactory<WithSaasMgrPrincipal> {

	@Override
    public SecurityContext createSecurityContext(WithSaasMgrPrincipal annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication token;
        if (annotation.noAuth()) {
            token = new AnonymousAuthenticationToken(
                    "anonymousUser", "anonymousUser", Lists.newArrayList(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));
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

	private List<SubscriptionConstraint> getConstraints(String[] constraints) {
		return Arrays
				.stream(constraints)
				.map(con -> {
					String[] splitted = con.split(";");
					return new SubscriptionConstraint(splitted[0], Double
							.valueOf(splitted[1]), Double.valueOf(splitted[2]));
				}).collect(Collectors.toList());
	}
}