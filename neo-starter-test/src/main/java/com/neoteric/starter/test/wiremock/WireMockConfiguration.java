package com.neoteric.starter.test.wiremock;

import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WireMockConfiguration {

    @Bean
    public ServerList<Server> ribbonServerList() {
        return new VeryStaticServerList();
    }

}
