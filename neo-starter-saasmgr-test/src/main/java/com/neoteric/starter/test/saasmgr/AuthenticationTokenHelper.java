package com.neoteric.starter.test.saasmgr;

import com.neoteric.starter.saasmgr.model.SubscriptionConstraint;
import org.assertj.core.util.Lists;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class AuthenticationTokenHelper {

    private AuthenticationTokenHelper() {
        // prevents instantiation
    }

    public static Authentication anonymousToken() {
        return new AnonymousAuthenticationToken(
                "anonymousUser", "anonymousUser", Lists.newArrayList(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));
    }

    public static List<SubscriptionConstraint> getConstraints(String... constraints) {
        return Arrays.stream(constraints)
                .map(con -> {
                    String[] splitted = con.split(";");
                    return new SubscriptionConstraint(splitted[0], Double.valueOf(splitted[1]), Double.valueOf(splitted[2]));
                })
                .collect(Collectors.toList());
    }
}
