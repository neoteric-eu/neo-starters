package com.neoteric.starter.test.wiremock;

import com.google.common.collect.Lists;
import com.neoteric.starter.test.wiremock.ribbon.RibbonTestServer;
import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.LoadBalancerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.EnvironmentTestUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;

import static com.neoteric.starter.test.StarterTestProfiles.WIREMOCK;

@Configuration
@Profile(WIREMOCK)
public class WiremockAutoConfiguration {

    @Autowired
    Environment environment;

    @Bean
    @Primary
    public ILoadBalancer ribbonLoadBalancer() {
        BaseLoadBalancer balancer = LoadBalancerBuilder.newBuilder()
                .buildFixedServerListLoadBalancer(Lists.newArrayList(RibbonTestServer.get()));
        return balancer;
    }

    @PostConstruct
    public void setUpWiremock() {
        EnvironmentTestUtils.addEnvironment((ConfigurableEnvironment) environment, "ribbon.eureka.enabled=false");
    }
}