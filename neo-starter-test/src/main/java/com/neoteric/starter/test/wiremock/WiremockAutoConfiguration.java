package com.neoteric.starter.test.wiremock;

import com.neoteric.starter.test.wiremock.ribbon.RibbonStaticServer;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.test.EnvironmentTestUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;

import static com.neoteric.starter.test.StarterTestProfiles.WIREMOCK;

@Configuration
@Profile(WIREMOCK)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
public class WiremockAutoConfiguration {

    @Autowired
    Environment environment;

    @Bean
    public ServerList<Server> ribbonServerList() {
        return new RibbonStaticServer();
    }

    @PostConstruct
    public void setUpWiremock() {
        EnvironmentTestUtils.addEnvironment((ConfigurableEnvironment)environment, "ribbon.eureka.enabled=false");
    }
}