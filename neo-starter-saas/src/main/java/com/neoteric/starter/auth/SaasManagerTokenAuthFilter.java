package com.neoteric.starter.auth;

import com.neoteric.starter.auth.basics.UserAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SaasManagerTokenAuthFilter extends OncePerRequestFilter {

    @Autowired
    UserAuthenticationFetcher userAuthenticationFetcher;
    private static final Logger LOG = LoggerFactory.getLogger(SaasManagerTokenAuthFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = request.getHeader(NeoHeaders.AUTHORIZATION.getValue());
        String customerId = request.getHeader(NeoHeaders.X_CUSTOMER_ID.getValue());
        UserAuthentication userAuthentication = userAuthenticationFetcher.getUserAuthentication(token, customerId);

        Authentication authentication = createAuthentication(userAuthentication.getUsername(), userAuthentication.getFeatures(), token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserAuthenticationHolder.set(userAuthentication);
        try {
            filterChain.doFilter(request, response);
        } finally {
            UserAuthenticationHolder.reset();
            LOG.debug("Cleared thread-bound user authentication: {}", request);
        }
    }

    public Authentication createAuthentication(String username, List<String> features, String token) {

        return new PreAuthenticatedAuthenticationToken(
                username,
                token,
                features.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList()));
    }
}
