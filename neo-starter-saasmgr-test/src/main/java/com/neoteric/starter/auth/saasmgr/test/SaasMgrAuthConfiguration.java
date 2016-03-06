package com.neoteric.starter.auth.saasmgr.test;

import com.neoteric.starter.auth.saasmgr.SaasMgrAuthenticator;
import com.neoteric.starter.auth.saasmgr.SaasMgrPrincipal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.util.Arrays;

@Configuration
@Profile("fixedSaasMgr")
public class SaasMgrAuthConfiguration {

    @Value("${neostarter.test.saasMgr.customerId}")
    private String customerId = SaasMgrTestDefaults.CUSTOMER_ID;

    @Value("${neostarter.test.saasMgr.customerName}")
    private String customerName = SaasMgrTestDefaults.CUSTOMER_NAME;

    @Value("${neostarter.test.saasMgr.email}")
    private String email = SaasMgrTestDefaults.EMAIL;

    @Value("${neostarter.test.saasMgr.userId}")
    private String userId = SaasMgrTestDefaults.USER_ID;

    @Value("${neostarter.test.saasMgr.features}")
    private String[] features;


    @Bean
    @Primary
    SaasMgrAuthenticator saasMgrConnector() {
        SaasMgrPrincipal saasDetails = new SaasMgrPrincipal.Builder()
                .customerId(customerId)
                .customerName(customerName)
                .email(email)
                .userId(userId)
                .features(Arrays.asList(features))
                .build();
        return new TestSaasMgrAuthenticator(saasDetails);
    }

}
