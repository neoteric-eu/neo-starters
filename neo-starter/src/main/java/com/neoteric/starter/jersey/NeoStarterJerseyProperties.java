package com.neoteric.starter.jersey;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("neostarter.jersey")
public class NeoStarterJerseyProperties {

    private String[] packagesToScan;

    private boolean logEndpointsOnStartup = true;
}
