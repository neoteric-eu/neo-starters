package com.neoteric.starter.saasmgr.filter;

import com.neoteric.starter.saasmgr.auth.SaasMgrAuthenticationToken;
import com.neoteric.starter.saasmgr.client.feign.SaasMgr;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@AllArgsConstructor
public class SaasMgrAuthenticationFilter extends OncePerRequestFilter {

    private final SaasMgrAuthenticationMatcher authenticationMatcher;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return !authenticationMatcher.matches(request);
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