package com.neoteric.starter.test.saasmgr;

import com.neoteric.starter.saasmgr.auth.SaasMgrAuthenticator;
import com.neoteric.starter.saasmgr.principal.SaasMgrPrincipal;
import com.neoteric.starter.test.saasmgr.auth.StaticSaasMgrPrincipal;
import com.neoteric.starter.test.saasmgr.auth.TestSaasMgrAuthenticator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import static com.neoteric.starter.test.saasmgr.SaasMgrStarterConstants.LOG_PREFIX;
import static com.neoteric.starter.test.saasmgr.StarterSaasTestProfiles.FIXED_SAAS_MGR;

@Slf4j
@Configuration
@Profile(FIXED_SAAS_MGR)
public class FixedSaasMgrAutoConfiguration {

    @Bean
    @Primary
    SaasMgrAuthenticator saasMgrConnector() {
        SaasMgrPrincipal saasDetails = new StaticSaasMgrPrincipal();
        LOG.debug("{}Using StaticSaasMgrPrincipal", LOG_PREFIX);
        return new TestSaasMgrAuthenticator(saasDetails);
    }
}
