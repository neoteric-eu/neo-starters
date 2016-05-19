package com.neoteric.starter.saasmgr.auth;

import com.neoteric.starter.saasmgr.client.SaasMgrClient;
import com.neoteric.starter.saasmgr.model.Customer;
import com.neoteric.starter.saasmgr.model.LoginData;
import com.neoteric.starter.saasmgr.principal.DefaultSaasMgrPrincipal;
import com.neoteric.starter.saasmgr.principal.SaasMgrPrincipal;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

@AllArgsConstructor
public class SaasMgrAuthenticationProvider implements AuthenticationProvider {

    private final SaasMgrClient saasMgrClient;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        SaasMgrAuthenticationToken saasMgrToken = (SaasMgrAuthenticationToken)authentication;
        String customerId = String.valueOf(saasMgrToken.getPrincipal());
        String token = String.valueOf(saasMgrToken.getCredentials());

        LoginData loginData = saasMgrClient.getLoginInfo(token, customerId);

        SaasMgrPrincipal saasMgrPrincipal = extractAuthenticationDetails(loginData, customerId);
        return new SaasMgrAuthenticationToken(saasMgrPrincipal, saasMgrToken.getCredentials(), saasMgrPrincipal.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return SaasMgrAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private SaasMgrPrincipal extractAuthenticationDetails(LoginData loginData, String customerId) {
        if (loginData.getUser() == null) {
            throw new UsernameNotFoundException("No user data found in SaasMgr response");
        }

        String userId = loginData.getUser().getId();
        String email = loginData.getUser().getEmail();

        Optional<Customer> customer = loginData.getUser().getCustomers().stream()
                .filter(customerInfo -> customerId.equals(customerInfo.getCustomerId()))
                .findFirst();

        Customer foundedCustomer = customer.orElseThrow(() -> new UsernameNotFoundException("No customer data found in SaasMgr response"));
        return DefaultSaasMgrPrincipal.builder()
                .userId(userId)
                .email(email)
                .constraints(foundedCustomer.getConstraints())
                .customerId(foundedCustomer.getCustomerId())
                .customerName(foundedCustomer.getCustomerName())
                .features(foundedCustomer.getFeatureKeys())
                .status(foundedCustomer.getStatus())
                .build();
    }
}