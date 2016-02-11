package com.neoteric.starter.auth;

import com.neoteric.starter.auth.basics.UserAuthentication;
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
    private UserAuthenticationFetcher userAuthenticationFetcher;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = request.getHeader(NeoHeaders.AUTHORIZATION.getValue());
        String customerId = request.getHeader(NeoHeaders.X_CUSTOMER_ID.getValue());
        UserAuthentication userAuthentication = userAuthenticationFetcher.getUserAuthentication(token, customerId);

        Authentication authentication = createAuthentication(userAuthentication.getEmail(), userAuthentication.getFeatures(), token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserAuthenticationHolder.set(userAuthentication);
        try {
            filterChain.doFilter(request, response);
        } finally {
            UserAuthenticationHolder.reset();
        }
    }

    public Authentication createAuthentication(String email, List<String> features, String token) {

        return new PreAuthenticatedAuthenticationToken(
                email,
                token,
                features.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList()));
    }
}
