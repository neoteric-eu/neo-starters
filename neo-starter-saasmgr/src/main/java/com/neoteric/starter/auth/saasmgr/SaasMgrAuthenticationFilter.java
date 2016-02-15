package com.neoteric.starter.auth.saasmgr;

import com.neoteric.starter.auth.saasmgr.client.SaasMgrClient;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SaasMgrAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public SaasMgrAuthenticationFilter() {
        super(ContainsSaasMgrHeadersMatcher.INSTANCE);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        String customerId = request.getHeader(SaasMgrClient.CUSTOMER_ID_HEADER);
        String token = request.getHeader(SaasMgrClient.AUTHORIZATION_HEADER);
        return new SaasMgrAuthenticationToken(customerId, token);
    }

    @Override
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }
}
