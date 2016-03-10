package com.neoteric.starter.auth.saasmgr.test;

import com.neoteric.starter.auth.SaasMgrSecurityAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.test.EnvironmentTestUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;

@Configuration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@AutoConfigureBefore(SaasMgrSecurityAutoConfiguration.class)
public class DisableCacheAutoConfiguration {

    @Autowired
    Environment environment;

    @PostConstruct
    public void disableCache() {
        EnvironmentTestUtils.addEnvironment((ConfigurableEnvironment)environment, "neostarter.saasmgr.cache.enabled=false");
    }
}
