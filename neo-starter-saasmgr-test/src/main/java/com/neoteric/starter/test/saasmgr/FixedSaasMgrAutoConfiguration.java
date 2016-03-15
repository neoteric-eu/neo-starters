package com.neoteric.starter.test.saasmgr;

import com.neoteric.starter.saasmgr.auth.SaasMgrAuthenticator;
import com.neoteric.starter.saasmgr.auth.DefaultSaasMgrPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.util.Arrays;

import static com.neoteric.starter.test.saasmgr.SaasMgrStarterConstants.LOG_PREFIX;

@Slf4j
@Configuration
@Profile(StarterSaasTestProfiles.FIXED_SAAS_MGR)
public class FixedSaasMgrAutoConfiguration {

    @Value("${neostarter.test.saasmgr.customerId:customerId}")
    private String customerId;

    @Value("${neostarter.test.saasmgr.customerName:customerName}")
    private String customerName;

    @Value("${neostarter.test.saasmgr.email:email}")
    private String email;

    @Value("${neostarter.test.saasmgr.userId:userId}")
    private String userId;

    @Value("${neostarter.test.saasmgr.features:}")
    private String[] features;

    @Bean
    @Primary
    SaasMgrAuthenticator saasMgrConnector() {
        DefaultSaasMgrPrincipal saasDetails = new DefaultSaasMgrPrincipal.Builder()
                .customerId(customerId)
                .customerName(customerName)
                .email(email)
                .userId(userId)
                .features(Arrays.asList(features))
                .build();

        LOG.debug("{}Using SaasMgrPrincipals: {}", LOG_PREFIX, saasDetails);
        return new TestSaasMgrAuthenticator(saasDetails);
    }
}
