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
@Profile(SaasStarterTestProfiles.FIXED_SAAS_MGR)
public class FixedSaasMgrAutoConfiguration {

    @Value("${neostarter.saasMgr.customerId:customerId}")
    private String customerId;

    @Value("${neostarter.saasMgr.customerName:customerName}")
    private String customerName;

    @Value("${neostarter.saasMgr.email:email}")
    private String email;

    @Value("${neostarter.saasMgr.userId:userId}")
    private String userId;

    @Value("${neostarter.saasMgr.features:}")
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
