package com.neoteric.starter.saasmgr.auth;

import com.neoteric.starter.saasmgr.client.SaasMgrClient;
import com.neoteric.starter.saasmgr.model.Customer;
import com.neoteric.starter.saasmgr.model.LoginData;
import com.neoteric.starter.saasmgr.principal.DefaultSaasMgrPrincipal;
import com.neoteric.starter.saasmgr.principal.SaasMgrPrincipal;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

@Slf4j
@AllArgsConstructor
public class DefaultSaasMgrAuthenticator implements SaasMgrAuthenticator {

    private final SaasMgrClient saasMgrClient;

    @Override
    public SaasMgrPrincipal authenticate(String token, String customerId) {
        LoginData loginData = saasMgrClient.getLoginInfo(token, customerId);
        return extractAuthenticationDetails(loginData, customerId);
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