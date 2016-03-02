package com.neoteric.starter.auth.saasmgr.test;

import com.neoteric.starter.auth.saasmgr.SaasMgrAuthenticator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class SaasMgrTestConfiguration {

    @Bean
    @Primary
    SaasMgrAuthenticator testSaasMgrConnector() {
        return new TestSaasMgrAuthenticator();
    }
}
