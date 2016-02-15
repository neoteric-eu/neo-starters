package com.neoteric.starter.auth.saasmgr;

import com.neoteric.starter.auth.saasmgr.client.SaasMgrClient;
import com.neoteric.starter.auth.saasmgr.client.model.Customer;
import com.neoteric.starter.auth.saasmgr.client.model.LoginData;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@EnableFeignClients
public class SaasMgrConnector {

    @Autowired
    private SaasMgrClient saasMgrClient;

    public SaasMgrAuthenticationDetails getSaasMgrAuthenticationDetails(String token, String customerId) {
        LoginData loginData = null;
        try {
            loginData = saasMgrClient.getLoginInfo(token, customerId);
        } catch (FeignException e) {
            if (e.status() == HttpStatus.UNAUTHORIZED.value()) {
                throw new BadCredentialsException("SaasMgr authentication failed.", e);
            } else if (e.status() > HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                throw new AuthenticationServiceException("SaasMgr authentication error.", e);
            }
        }
        return extractAuthenticationDetails(loginData, customerId);
    }

    private SaasMgrAuthenticationDetails extractAuthenticationDetails(LoginData loginData, String customerId) {
        if (loginData.getUser() == null) {
            throw new UsernameNotFoundException("No user data found in SaasMgr response");
        }

        String userId = loginData.getUser().getId();
        String email = loginData.getUser().getEmail();

        Optional<Customer> customer = loginData.getUser().getCustomers().stream()
                .filter(customerInfo -> customerId.equals(customerInfo.getCustomerId()))
                .findFirst();

        Customer foundedCustomer = customer.orElseThrow(() -> new UsernameNotFoundException("No customer data found in SaasMgr response"));

        return SaasMgrAuthenticationDetails.builder()
                .userId(userId)
                .email(email)
                .customerId(foundedCustomer.getCustomerId())
                .customerName(foundedCustomer.getCustomerName())
                .features(foundedCustomer.getFeatureKeys())
                .status(foundedCustomer.getStatus())
                .build();
    }
}