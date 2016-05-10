package com.neoteric.starter.saasmgr.filter;

import com.neoteric.starter.saasmgr.auth.SaasMgrAuthenticationToken;
import com.neoteric.starter.saasmgr.client.feign.SaasMgr;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SaasMgrAuthenticationFilter extends OncePerRequestFilter {

    private final String applicationPath;

    public SaasMgrAuthenticationFilter(String applicationPath) {
        this.applicationPath = applicationPath;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return new NegatedRequestMatcher(
                new AndRequestMatcher(
                        ContainsSaasMgrHeadersMatcher.INSTANCE,
                        new AntPathRequestMatcher(applicationPath + "/**")))
                .matches(request);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String customerId = request.getHeader(SaasMgr.CUSTOMER_ID_HEADER);
        String token = request.getHeader(SaasMgr.AUTHORIZATION_HEADER);
        Authentication authToken = new SaasMgrAuthenticationToken(customerId, token);
        SecurityContextHolder.getContext().setAuthentication(authToken);
        try {
            filterChain.doFilter(request, response);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}