package com.neoteric.starter.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class SaasManagerTokenAuthFilter extends OncePerRequestFilter {


    @Autowired
    AuthenticationCreator authenticationCreator;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = request.getHeader(NeoHeaders.AUTHORIZATION.getValue());
        String customerId = request.getHeader(NeoHeaders.X_CUSTOMER_ID.getValue());

        Authentication auth = authenticationCreator.createAuthentication(token, customerId);
        SecurityContextHolder.getContext().setAuthentication(auth);
        filterChain.doFilter(request, response);
    }
}
