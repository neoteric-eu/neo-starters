package com.neoteric.starter.auth.saasmgr;

import com.neoteric.starter.auth.saasmgr.client.SaasMgrClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SaasMgrAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final Logger LOG = LoggerFactory.getLogger(SaasMgrAuthenticationFilter.class);

    public SaasMgrAuthenticationFilter(String applicationPath) {
        super(new AndRequestMatcher(ContainsSaasMgrHeadersMatcher.INSTANCE, new AntPathRequestMatcher(applicationPath + "/**")));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        String customerId = request.getHeader(SaasMgrClient.CUSTOMER_ID_HEADER);
        String token = request.getHeader(SaasMgrClient.AUTHORIZATION_HEADER);
        return new SaasMgrAuthenticationToken(customerId, token);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        LOG.warn("UNSUCESS {}, {}, {}", request, response, failed);
        super.unsuccessfulAuthentication(request, response, failed);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        LOG.warn("SUCCESS");
        super.successfulAuthentication(request, response, chain, authResult);
    }


}