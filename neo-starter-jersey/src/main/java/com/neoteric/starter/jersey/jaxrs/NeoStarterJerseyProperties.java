package com.neoteric.starter.jersey.jaxrs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("neostarter.jersey")
public class NeoStarterJerseyProperties {
    private boolean logEndpointsOnStartup = true;
}
